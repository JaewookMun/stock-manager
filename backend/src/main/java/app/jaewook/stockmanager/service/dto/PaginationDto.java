package app.jaewook.stockmanager.service.dto;

public class PaginationDto {
    public record Request(
        int page,
        int size
    ) {}

    public record Response(
        long totalCount,
        int currentPage
    ) {}
}
