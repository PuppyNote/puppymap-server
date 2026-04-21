package com.puppymapserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppymapserver.favorite.controller.FavoriteController;
import com.puppymapserver.favorite.service.FavoriteService;
import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.like.service.PlaceLikeService;
import com.puppymapserver.place.controller.AdminPlaceController;
import com.puppymapserver.place.controller.PlaceController;
import com.puppymapserver.place.service.AdminPlaceService;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.PlaceService;
import com.puppymapserver.review.controller.ReviewController;
import com.puppymapserver.review.service.ReviewService;
import com.puppymapserver.user.users.controller.MyPageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        PlaceController.class,
        AdminPlaceController.class,
        ReviewController.class,
        FavoriteController.class,
        MyPageController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected PlaceService placeService;

    @MockitoBean
    protected PlaceReadService placeReadService;

    @MockitoBean
    protected AdminPlaceService adminPlaceService;

    @MockitoBean
    protected ReviewService reviewService;

    @MockitoBean
    protected PlaceLikeService placeLikeService;

    @MockitoBean
    protected FavoriteService favoriteService;

    @MockitoBean
    protected SecurityService securityService;
}
