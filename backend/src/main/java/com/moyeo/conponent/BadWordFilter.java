package com.moyeo.conponent;

import com.moyeo.dto.WordInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class BadWordFilter {
//    private final HashSet<String> filter = new HashSet<>(Arrays.asList("바보","멍청이"));
    private final ArrayList<String> filter = new ArrayList<>(Arrays.asList("행복"));
    public List<WordInfo> badWord(List<WordInfo> wordInfoList){
        List<WordInfo> badWords = new ArrayList<>();

        for (int i = 0;i<wordInfoList.size();i++){
            for (int j = 0; j < filter.size(); j++) {
                if(wordInfoList.get(i).getWord().contains(filter.get(j))){
                    badWords.add(WordInfo.builder()
                            .startTime(wordInfoList.get(i).getStartTime())
                            .endTime(wordInfoList.get(i).getEndTime())
                            .word(wordInfoList.get(i).getWord()).build());
                }
            }
//            if(filter.contains(wordInfoList.get(i).getWord())){
//                badWords.add(WordInfo.builder()
//                        .startTime(wordInfoList.get(i).getStartTime())
//                        .endTime(wordInfoList.get(i).getEndTime())
//                        .word(wordInfoList.get(i).getWord()).build());
//            }
        }
        return badWords;
    }
}
