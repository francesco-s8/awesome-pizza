package it.s8.awesomepizza.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public record OrderRequest(@NotNull String user, @NotEmpty List<String> pizzas) {
}
