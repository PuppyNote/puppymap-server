package docs.storage;

import com.puppymapserver.storage.controller.StorageController;
import com.puppymapserver.storage.service.S3StorageService;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StorageControllerDocsTest extends RestDocsSupport {

    private final S3StorageService s3StorageService = mock(S3StorageService.class);

    @Override
    protected Object initController() {
        return new StorageController(s3StorageService);
    }

    @DisplayName("파일 업로드 API")
    @Test
    void 파일_업로드() throws Exception {
        given(s3StorageService.upload(any()))
                .willReturn("https://s3.example.com/puppy-walk/image.jpg");

        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/storage")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("storage-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("file").description("업로드할 파일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("업로드된 파일 URL")
                        )
                ));
    }
}
