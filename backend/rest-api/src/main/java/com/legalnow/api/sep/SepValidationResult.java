package com.legalnow.api.sep;

import java.util.List;

public record SepValidationResult(
    boolean found,
    List<SepProfesionistaDto> matches
) {}
