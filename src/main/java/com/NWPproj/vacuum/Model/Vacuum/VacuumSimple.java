package com.NWPproj.vacuum.Model.Vacuum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacuumSimple {
    private int addedBy;
    private String name;
}
