package com.woomulwoomul.core.common.utils

import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import org.springframework.ui.Model

class ModelUtils {

    companion object {

        /**
         * 페이징 데이터 모델에 추가
         * @param pageOffsetRequest 페이징 오프셋 요청
         * @param response 페이징 데이터 응답
         * @param model 모델
         */
        fun <T> addPageDataAttribute(pageOffsetRequest: PageOffsetRequest, response: PageData<T>, model: Model) {
            model.addAttribute("data", response.data)
            model.addAttribute("currentPage", pageOffsetRequest.from + 1)
            model.addAttribute("totalPage",
                (response.total + pageOffsetRequest.size - 1) / pageOffsetRequest.size)
        }

        fun addAttribute(response: Any, model: Model) {
            model.addAttribute("response", response)
        }
    }
}