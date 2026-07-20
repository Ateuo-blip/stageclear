package io.stageclear.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResult<T> {
    private List<T> records;
    private Long nextCursor;
    private Boolean hasMore;

    public static <T> CursorPageResult<T> of(List<T> records, Long nextCursor, boolean hasMore) {
        return CursorPageResult.<T>builder()
                .records(records)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }
}
