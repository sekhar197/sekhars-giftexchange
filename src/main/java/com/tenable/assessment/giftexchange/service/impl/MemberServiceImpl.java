package com.tenable.assessment.giftexchange.service.impl;

import com.tenable.assessment.dto.MemberDto;
import com.tenable.assessment.giftexchange.domain.Member;
import com.tenable.assessment.giftexchange.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MemberServiceImpl implements MemberService {

    private final Set<Member> memberDataSet;
    public MemberServiceImpl(Set<Member> memberDataSet){
        this.memberDataSet = memberDataSet;
    }
    /**
     * @return
     */
    @Override
    public List<MemberDto> getAllMembers() {
        List<MemberDto> memberDtoList = Collections.emptyList();
        if(!CollectionUtils.isEmpty(memberDataSet)) {
           for(Member member : memberDataSet ) {
               memberDtoList.add(mapToMemberDto(member));
           }
        }

        return memberDtoList;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public MemberDto getMemberById(String id) {
        Optional<Member> memberOptional = memberDataSet.stream().filter(member-> member.getMemberId().equals(id)).findFirst();
        if(memberOptional.isEmpty()){
            //TODO: throw 404 not found exception
        }
       return mapToMemberDto(memberOptional.get());
    }

    /**
     * @param memberDto
     * @return
     */
    @Override
    public void createMember(MemberDto memberDto) {
        //taking memberId from user input to keep it simple, would prefer UUID to be generated by api
        Optional<Member> memberOptional = memberDataSet.stream().filter(member-> member.getMemberId().equals(memberDto)).findFirst();
        if(memberOptional.isPresent()) {
            //TODO : throw 409 conflict
        }

        Member member = mapToMember(memberDto);
        memberDataSet.add(member);
    }

    /**
     * @param memberDto
     * @return
     */
    @Override
    public MemberDto updateMember(MemberDto memberDto) {
        Member member = mapToMember(memberDto);
        Optional<Member> memberOptional = memberDataSet.stream()
                .filter(memberItem-> memberItem.getMemberId().equals(memberDto.getId())).findFirst();
        if(memberOptional.isEmpty()) {
            //return 404 not found
        }
        memberOptional.get().setName(memberDto.getName());

        return getMemberById(memberDto.getId());
    }

    /**
     * @param id
     */
    @Override
    public void deleteMember(String id) {
        Optional<Member> memberOptional = memberDataSet.stream()
                .filter(memberItem-> memberItem.getMemberId().equals(id))
                .findFirst();
        if(memberOptional.isEmpty()) {
            //return 404 not found
        }
        memberDataSet.remove(memberOptional.get());

    }

    private MemberDto mapToMemberDto(Member member) {
        //would use some pojo mapper like Dozer mapper for mapping domain objects to DTOs
        MemberDto memberDto = new MemberDto();
        memberDto.setId(member.getMemberId());
        memberDto.setName(member.getName());

        return memberDto;
    }

    private Member mapToMember(MemberDto memberDto) {
        //would use some pojo mapper like Dozer mapper
        Member member = new Member();
        member.setMemberId(memberDto.getId());
        member.setName(memberDto.getName());

        return member;
    }
}
