package com.legalnow.api.sep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SepProfesionistaDto(
    @JsonProperty("numCedula") String numCedula,
    @JsonProperty("nombre") String nombre,
    @JsonProperty("paterno") String primerApellido,
    @JsonProperty("materno") String segundoApellido,
    @JsonProperty("curp") String curp,
    @JsonProperty("titulo") String titulo,
    @JsonProperty("desIne") String institucion,
    @JsonProperty("anioExamen") String anioExamen
) {}
