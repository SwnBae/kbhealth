package kb.health.loader.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FoodResponse(
        Response response
) {
    public record Response(
            Header header,

            Body body
    ) {
        public record Header(
                String resultCode,

                String resultMsg,

                String type
        ) {
        }

        public record Body(
                List<FoodItem> items,

                String totalCount,

                String numOfRows,

                String pageNo
        ) {
        }
    }
}
