package com.help.hyozason_backend.mapper.helpreward;

import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.dto.helpreward.HelpRewardDTO;
import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import com.help.hyozason_backend.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") //* (componentModel = "spring") 이게 없으면 스프링 bean 으로 등록되지 않으므로 명시해 줘야함.
public interface HelpRewardMapper extends GenericMapper<HelpRewardDTO, HelpRewardEntity> {
    HelpRewardMapper INSTANCE = Mappers.getMapper(HelpRewardMapper.class);
}
