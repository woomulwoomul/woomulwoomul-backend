package com.woomulwoomul.woomulwoomulbackend.common.utils

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.FILE_FIELD_REQUIRED
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.IMAGE_TYPE_UNSUPPORTED
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.util.stream.Stream

class FileUtilsTest {

    @ParameterizedTest(name = "[{index}] 이미지 파일 타입 검증이 정상 작동한다")
    @MethodSource("providerValidValidateImageFileType")
    @DisplayName("이미지 파일 타입 검증이 정상 작동한다")
    fun givenValid_whenValidateImageFileType_thenReturn(file: MultipartFile) {
        // when & then
        assertDoesNotThrow { FileUtils.validateImageFileType(file) }
    }

    @DisplayName("null로 이미지 파일 타입 검증을 하면 예외가 발생한다")
    @Test
    fun givenNull_whenValidateImageFileType_thenThrow() {
        // given
        val file: MultipartFile? = null

        // when & then
        assertThatThrownBy { FileUtils.validateImageFileType(file) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FILE_FIELD_REQUIRED)
    }

    @ParameterizedTest(name = "[{index}] 잘못된 파일 타입으로 이미지 파일 타입 검증을 하면 예외가 발생한다")
    @MethodSource("providerInvalidValidateImageFileType")
    @DisplayName("잘못된 파일 타입으로 이미지 파일 타입 검증을 하면 예외가 발생한다")
    fun givenInvalidFileType_whenValidateImageFileType_thenThrow(file: MultipartFile) {
        // when & then
        assertThatThrownBy { FileUtils.validateImageFileType(file) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(IMAGE_TYPE_UNSUPPORTED)
    }

    companion object {
        @JvmStatic
        fun providerValidValidateImageFileType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(MockMultipartFile("file", "file.png", "image/png", ByteArray(1))),
                Arguments.of(MockMultipartFile("file", "file.jpg", "image/jpg", ByteArray(1))),
                Arguments.of(MockMultipartFile("file", "file.jpeg", "image/jpeg", ByteArray(1)))
            )
        }

        @JvmStatic
        fun providerInvalidValidateImageFileType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(MockMultipartFile("file", "file.gif", "image/gif", ByteArray(1))),
                Arguments.of(MockMultipartFile("file", "file", null, ByteArray(1))),
                Arguments.of(MockMultipartFile("file", "file", "", ByteArray(1)))
            )
        }
    }
}