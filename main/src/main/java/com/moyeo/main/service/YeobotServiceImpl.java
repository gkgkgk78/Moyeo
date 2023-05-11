package com.moyeo.main.service;

import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.MoyeoPostRepository;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.TimeLineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class YeobotServiceImpl implements YeobotService{

    private final PostRepository postRepository;
    private final MoyeoPostRepository moyeoPostRepository;
    private final TimeLineRepository timeLineRepository;

    public List<String[]> findLatestAddress(Long userId) {
        // Post와 MoyeoPost에서 각각 최신 게시글의 id 가져오기
        Long latestPostId = postRepository.findLatestPost(userId);
        Long latestMoyeoPostId = moyeoPostRepository.findLatestMoyeoPost(userId);

        // 최신 게시글의 id를 이용하여 주소 정보를 가져오기
        List<String[]> latestPostAddress = postRepository.findAddressById(latestPostId, userId);
        List<String[]> latestMoyeoPostAddress = moyeoPostRepository.findAddressByMoyeoPostId(latestMoyeoPostId);

        // 두 게시글 중에서 더 최신의 게시글을 선택해서 주소 반환
        if(latestPostAddress.isEmpty() && latestMoyeoPostAddress.isEmpty()) {
            throw new BaseException(ErrorMessage.NOT_EXIST_LATEST_ADDRESS);
        }
        else if(latestMoyeoPostAddress.isEmpty()) {
            return latestPostAddress;
        }
        else if(latestPostAddress.isEmpty()) {
            return latestMoyeoPostAddress;
        }
        else {
            LocalDateTime latestPostTime = postRepository.findCreateTimeByPostId(latestPostId);
            LocalDateTime latestMoyeoPostTime = moyeoPostRepository.findCreateTimeByMoyeoPostId(latestMoyeoPostId);
            if (latestPostTime == null || latestMoyeoPostTime == null) {
                throw new BaseException(ErrorMessage.NOT_EXIST_LATEST_TIME);
            }
            if (latestPostTime.isAfter(latestMoyeoPostTime)) {
                return latestPostAddress;
            } else {
                return latestMoyeoPostAddress;
            }
        }
    }


    public String getLatestTimelineStatus(Long userId) {
        int flag = timeLineRepository.findLatestTimelineStatus(userId);
        String status;

        if(flag == 0){
            status = "여행중";
        }else{
            status = "여행중이 아님";
        }

        return status;
    }


}
