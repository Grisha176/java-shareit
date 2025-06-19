package request;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestCreateDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void testItemRequestCreateDtoSerialization() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime nowTruncated = LocalDateTime.of(2023,10,26,10,00);
        ItemRequestDto itemRequestCreateDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test")
                .created(nowTruncated.toString())
                .build();

        String jsonContent = itemRequestCreateDtoJacksonTester.write(itemRequestCreateDto).getJson();

        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).contains("\"description\":\"Test\"");
    }

    @Test
    void testItemRequestDtoDeserialization() throws IOException {
        String json = "{\"id\":1,\"description\":\"Test\",\"created\":\"2023-10-26T10:00:00\",\"items\":[]}";
        ItemRequestDto itemRequestDto = itemRequestDtoJacksonTester.parse(json).getObject();

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Test");
        assertThat(itemRequestDto.getItems()).isEmpty();
    }


    @Test
    void testItemRequestCreateDtoSerialization_emptyDescription() throws IOException {
        ItemRequestDto itemRequestCreateDto = ItemRequestDto.builder()
                .id(1L)
                .description(null)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .build();

        String jsonContent = itemRequestCreateDtoJacksonTester.write(itemRequestCreateDto).getJson();

        assertThat(jsonContent).contains("\"description\":null");
    }

    @Test
    void testItemRequestCreateDtoDeserialization_emptyDescription() throws IOException {
        String json = "{\"id\":1,\"description\":null,\"created\":\"2023-10-27T10:00:00\"}";
        ItemRequestDto itemRequestCreateDto = itemRequestCreateDtoJacksonTester.parse(json).getObject();

        assertThat(itemRequestCreateDto.getDescription()).isNull();
        assertThat(itemRequestCreateDto.getId()).isEqualTo(1L);
    }

    @Test
    void testItemRequestDtoSerialization_emptyItemList() throws IOException {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test")
                .created(LocalDateTime.parse("2023-10-27T10:00:00", FORMATTER).toString())
                .items(Collections.emptyList())
                .build();

        String jsonContent = itemRequestDtoJacksonTester.write(itemRequestDto).getJson();

        assertThat(jsonContent).contains("\"items\":[]");
    }

    @Test
    void testItemRequestDtoDeserialization_missingFields() throws IOException {
        String json = "{\"description\":\"Test\"}";
        ItemRequestDto itemRequestDto = itemRequestDtoJacksonTester.parse(json).getObject();

        assertThat(itemRequestDto.getDescription()).isEqualTo("Test");
    }

    @Test
    void testItemRequestCreateDtoSerialization_blankDescription() throws IOException {
        ItemRequestDto itemRequestCreateDto = ItemRequestDto.builder()
                .id(1L)
                .description(" ")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .build();

        String jsonContent = itemRequestCreateDtoJacksonTester.write(itemRequestCreateDto).getJson();
        assertThat(jsonContent).contains("\"description\":\" \"");
    }
}