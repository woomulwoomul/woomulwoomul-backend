package com.woomulwoomul.woomulwoomulbackend.common.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PagingData(long total, List<?> data) {
}
