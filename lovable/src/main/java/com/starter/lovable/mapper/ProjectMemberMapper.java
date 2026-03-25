package com.starter.lovable.mapper;

import com.starter.lovable.dto.member.MemberResponse;
import com.starter.lovable.entity.ProjectMember;
import com.starter.lovable.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "projectRole", constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    @Mapping(target = "userId",source ="user.id")
    @Mapping(target = "email",source ="user.email")
    @Mapping(target = "name",source ="user.name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);


}
