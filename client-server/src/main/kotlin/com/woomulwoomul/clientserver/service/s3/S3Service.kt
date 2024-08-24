package com.woomulwoomul.clientserver.service.s3

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.woomulwoomul.core.common.constant.ExceptionCode.SERVER_ERROR
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.utils.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class S3Service(
    private val amazonS3Client: AmazonS3Client,
) {
    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucket: String

    /**
     * 파일 업로드
     * @param file 파일
     * @throws FILE_FIELD_REQUIRED 400
     * @throws IMAGE_TYPE_UNSUPPORTED 415
     * @throws SERVER_ERROR 500
     * @return 파일 URL
     */
    fun uploadFile(file: MultipartFile?, pathName: String, fileName: String): String {
        FileUtils.validateImageFileType(file)

        val inputStream = file!!.inputStream
        val objectMetadata = ObjectMetadata().apply {
            contentLength = file.size
            contentType = file.contentType
        }

        return try {
            amazonS3Client.putObject(
                PutObjectRequest(bucket, "$pathName/$fileName", inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )
            amazonS3Client.getUrl(bucket, "$pathName/$fileName").toExternalForm()
        } catch (e: IOException) {
            throw CustomException(SERVER_ERROR, e.cause)
        }
    }
}