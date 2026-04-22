package com.puppymapserver.redis.service;

import com.puppymapserver.like.entity.PlaceLikeRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlaceLikeRedisService {

    private final RedisService redisService;

    static final Duration USER_LIKED_TTL = Duration.ofDays(1);
    static final Duration PLACE_COUNT_TTL = Duration.ofDays(1);

    // KEYS[1] = user:liked:{userId}:{placeId}
    // KEYS[2] = place:like:count:{placeId}
    // KEYS[3] = place:like:dirty
    // KEYS[4] = place:like:delta:add:{placeId}
    // KEYS[5] = place:like:delta:remove:{placeId}
    // ARGV[1] = userId, ARGV[2] = placeId, ARGV[3] = likedTtl(초), ARGV[4] = countTtl(초)
    // return [liked(1=좋아요/0=취소), likeCount]
    @SuppressWarnings("unchecked")
    private static final DefaultRedisScript<List<Long>> TOGGLE_SCRIPT;

    static {
        TOGGLE_SCRIPT = new DefaultRedisScript<>();
        TOGGLE_SCRIPT.setScriptText(
                "local likedKey    = KEYS[1]\n" +
                "local countKey    = KEYS[2]\n" +
                "local dirtyKey    = KEYS[3]\n" +
                "local deltaAddKey = KEYS[4]\n" +
                "local deltaRemKey = KEYS[5]\n" +
                "local userId    = ARGV[1]\n" +
                "local placeId   = ARGV[2]\n" +
                "local likedTtl  = tonumber(ARGV[3])\n" +
                "local countTtl  = tonumber(ARGV[4])\n" +
                "local current = redis.call('GET', likedKey)\n" +
                "local liked\n" +
                "local likeCount\n" +
                "if current == '1' then\n" +
                "    redis.call('SET', likedKey, '0', 'EX', likedTtl)\n" +
                "    likeCount = redis.call('DECR', countKey)\n" +
                "    redis.call('SADD', deltaRemKey, userId)\n" +
                "    redis.call('SREM', deltaAddKey, userId)\n" +
                "    liked = 0\n" +
                "else\n" +
                "    redis.call('SET', likedKey, '1', 'EX', likedTtl)\n" +
                "    likeCount = redis.call('INCR', countKey)\n" +
                "    redis.call('SADD', deltaAddKey, userId)\n" +
                "    redis.call('SREM', deltaRemKey, userId)\n" +
                "    liked = 1\n" +
                "end\n" +
                "redis.call('EXPIRE', countKey, countTtl)\n" +
                "redis.call('SADD', dirtyKey, placeId)\n" +
                "return {liked, likeCount}"
        );
        TOGGLE_SCRIPT.setResultType((Class<List<Long>>) (Class<?>) List.class);
    }

    public boolean existsLikedCache(Long userId, Long placeId) {
        return redisService.hasKey(PlaceLikeRedisKey.USER_LIKED.of(userId, placeId));
    }

    public boolean existsCountCache(Long placeId) {
        return redisService.hasKey(PlaceLikeRedisKey.PLACE_LIKE_COUNT.of(placeId));
    }

    public void setLikedCache(Long userId, Long placeId, boolean liked) {
        redisService.setValue(
                PlaceLikeRedisKey.USER_LIKED.of(userId, placeId),
                liked ? "1" : "0",
                USER_LIKED_TTL
        );
    }

    public void setCountCache(Long placeId, long count) {
        redisService.setIfAbsent(
                PlaceLikeRedisKey.PLACE_LIKE_COUNT.of(placeId),
                String.valueOf(count),
                PLACE_COUNT_TTL
        );
    }

    public Map<Long, Boolean> getLikedStatusBatch(List<Long> placeIds, Long userId) {
        List<String> keys = placeIds.stream()
                .map(placeId -> PlaceLikeRedisKey.USER_LIKED.of(userId, placeId))
                .toList();

        List<String> values = redisService.mGet(keys);

        Map<Long, Boolean> result = new HashMap<>();
        for (int i = 0; i < placeIds.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                result.put(placeIds.get(i), "1".equals(value));
            }
        }
        return result;
    }

    public Map<Long, Long> getLikeCountBatch(List<Long> placeIds) {
        List<String> keys = placeIds.stream()
                .map(placeId -> PlaceLikeRedisKey.PLACE_LIKE_COUNT.of(placeId))
                .toList();

        List<String> values = redisService.mGet(keys);

        Map<Long, Long> result = new HashMap<>();
        for (int i = 0; i < placeIds.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                result.put(placeIds.get(i), Long.parseLong(value));
            }
        }
        return result;
    }

    public Optional<Boolean> getLikedStatus(Long placeId, Long userId) {
        String value = redisService.getValue(PlaceLikeRedisKey.USER_LIKED.of(userId, placeId));
        if (value == null) return Optional.empty();
        return Optional.of("1".equals(value));
    }

    public Optional<Long> getLikeCount(Long placeId) {
        String value = redisService.getValue(PlaceLikeRedisKey.PLACE_LIKE_COUNT.of(placeId));
        if (value == null) return Optional.empty();
        return Optional.of(Long.parseLong(value));
    }

    public Set<String> popDeltaAdd(Long placeId, long timestamp) {
        return popDelta(PlaceLikeRedisKey.DELTA_ADD.of(placeId),
                PlaceLikeRedisKey.DELTA_ADD_PROCESSING.of(placeId, timestamp));
    }

    public Set<String> popDeltaRemove(Long placeId, long timestamp) {
        return popDelta(PlaceLikeRedisKey.DELTA_REMOVE.of(placeId),
                PlaceLikeRedisKey.DELTA_REMOVE_PROCESSING.of(placeId, timestamp));
    }

    public List<Long> toggle(Long userId, Long placeId) {
        return redisService.execute(
                TOGGLE_SCRIPT,
                List.of(
                        PlaceLikeRedisKey.USER_LIKED.of(userId, placeId),
                        PlaceLikeRedisKey.PLACE_LIKE_COUNT.of(placeId),
                        PlaceLikeRedisKey.DIRTY.of(),
                        PlaceLikeRedisKey.DELTA_ADD.of(placeId),
                        PlaceLikeRedisKey.DELTA_REMOVE.of(placeId)
                ),
                String.valueOf(userId),
                String.valueOf(placeId),
                String.valueOf(USER_LIKED_TTL.getSeconds()),
                String.valueOf(PLACE_COUNT_TTL.getSeconds())
        );
    }

    private Set<String> popDelta(String deltaKey, String processingKey) {
        if (!redisService.hasKey(deltaKey)) {
            return Set.of();
        }
        redisService.rename(deltaKey, processingKey);
        Set<String> members = redisService.sMembers(processingKey);
        redisService.delete(processingKey);
        return members == null ? Set.of() : members;
    }
}
