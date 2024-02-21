package io.flowpay.flowpayinterview.mapper;

import io.flowpay.flowpayinterview.model.dto.CompanyDTO;
import io.flowpay.flowpayinterview.model.dto.RepresentativeDTO;
import io.flowpay.flowpayinterview.model.entity.Company;
import io.flowpay.flowpayinterview.model.entity.Representative;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    CompanyDTO companyToDto(Company company);
    Company companyDtoToEntity(CompanyDTO companyDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "representatives", ignore = true)
    void updateCompanyFromDto(CompanyDTO dto, @MappingTarget Company entity);
    @AfterMapping
    default void afterUpdateCompanyFromDto(CompanyDTO dto, @MappingTarget Company entity) {
        if (dto.getRepresentatives() != null) {
            Set<Representative> representatives = dto.getRepresentatives().stream()
                    .map(this::representativeDtoToEntity)
                    .collect(Collectors.toSet());
            entity.setRepresentatives(representatives);
        }
    }
    RepresentativeDTO representativeToDto(Representative representative);
    Representative representativeDtoToEntity(RepresentativeDTO representativeDTO);
    @Mapping(target = "id", ignore = true)
    void updateRepresentativeFromDto(RepresentativeDTO dto, @MappingTarget Representative entity);

}
