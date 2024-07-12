package com.woomulwoomul.woomulwoomulbackend.common.utils

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.FILE_FIELD_REQUIRED
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.IMAGE_TYPE_UNSUPPORTED
import com.woomulwoomul.woomulwoomulbackend.common.constant.ServiceConstants.IMAGE_FILE_TYPES
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import org.springframework.web.multipart.MultipartFile

class FileUtility {

    companion object {

        /**
         * 이미지 파일 타입 검증
         * @param file 파일
         * @throws FILE_FIELD_REQUIRED 400
         * @throws IMAGE_TYPE_UNSUPPORTED 415
         */
        fun validateImageFileType(file: MultipartFile?) {
            requireNotNull(file) { throw CustomException(FILE_FIELD_REQUIRED) }

            val contentType = file.contentType ?: ""
            val fileType = contentType.split("/").getOrNull(1) ?: ""

            if (fileType !in IMAGE_FILE_TYPES.fields) {
                throw CustomException(IMAGE_TYPE_UNSUPPORTED)
            }
        }
    }
}