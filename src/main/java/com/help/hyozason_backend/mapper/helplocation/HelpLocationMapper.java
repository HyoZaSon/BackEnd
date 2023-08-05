package com.help.hyozason_backend.mapper.helplocation;

import com.help.hyozason_backend.dto.helplocation.HelpLocationDTO;
import com.help.hyozason_backend.dto.helpreward.HelpRewardDTO;
import com.help.hyozason_backend.entity.helplocation.HelpLocationEntity;
import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import com.help.hyozason_backend.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") //* (componentModel = "spring") 이게 없으면 스프링 bean 으로 등록되지 않으므로 명시해 줘야함.
public interface HelpLocationMapper extends GenericMapper<HelpLocationDTO, HelpLocationEntity> {
    HelpLocationMapper INSTANCE = Mappers.getMapper(HelpLocationMapper.class);
}
