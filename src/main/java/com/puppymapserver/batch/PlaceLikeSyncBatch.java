package com.puppymapserver.batch;

import com.puppymapserver.like.entity.PlaceLikeRedisKey;
import com.puppymapserver.like.repository.PlaceLikeJpaRepository;
import com.puppymapserver.redis.service.PlaceLikeRedisService;
import com.puppymapserver.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceLikeSyncBatch {

    private final PlaceLikeJpaRepository placeLikeJpaRepository;
    private final PlaceLikeRedisService placeLikeRedisService;
    private final RedisService redisService;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void syncLikesToDB() {
        if (!redisService.hasKey(PlaceLikeRedisKey.DIRTY.of())) {
            return;
        }

        String processingKey = PlaceLikeRedisKey.DIRTY_PROCESSING.of(System.currentTimeMillis());
        redisService.rename(PlaceLikeRedisKey.DIRTY.of(), processingKey);

        Set<String> dirtyPlaceIds = redisService.sMembers(processingKey);
        redisService.delete(processingKey);

        if (dirtyPlaceIds == null || dirtyPlaceIds.isEmpty()) {
            return;
        }

        log.info("[좋아요 배치 동기화] 처리 대상 장소 수: {}", dirtyPlaceIds.size());

        for (String placeIdStr : dirtyPlaceIds) {
            try {
                syncPlace(Long.parseLong(placeIdStr));
            } catch (Exception e) {
                log.error("[좋아요 배치 동기화] 장소 동기화 실패 placeId={}", placeIdStr, e);
                redisService.sAdd(PlaceLikeRedisKey.DIRTY.of(), placeIdStr);
            }
        }
    }

    public void syncPlace(Long placeId) {
        long timestamp = System.currentTimeMillis();

        Set<String> deltaAdd = placeLikeRedisService.popDeltaAdd(placeId, timestamp);
        Set<String> deltaRemove = placeLikeRedisService.popDeltaRemove(placeId, timestamp);

        if (deltaAdd.isEmpty() && deltaRemove.isEmpty()) {
            return;
        }

        List<Long> toInsert = deltaAdd.stream().map(Long::parseLong).toList();
        List<Long> toDelete = deltaRemove.stream().map(Long::parseLong).toList();

        toInsert.forEach(userId -> placeLikeJpaRepository.insertIgnore(placeId, userId));

        if (!toDelete.isEmpty()) {
            placeLikeJpaRepository.deleteByPlaceIdAndUserIdIn(placeId, toDelete);
        }

        log.info("[좋아요 배치 동기화] placeId={} insert={} delete={}", placeId, toInsert.size(), toDelete.size());
    }
}
