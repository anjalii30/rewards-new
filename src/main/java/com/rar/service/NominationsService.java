package com.rar.service;


import com.rar.model.NominationPojo;
import com.rar.model.Nominations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface NominationsService {

    ResponseEntity<?> nominationSave(NominationPojo nominationPojo);

    List<Nominations> GetData(Long rewardID) throws Exception;

    List<List<Nominations>> showToManager(String email,Long reward_id) throws Exception;

    void awardeeSelect(Map<String, Long[]> nomination_id);

    List<Map<String,String>> getAwardedPeople();

    List<List<Nominations>> showAllToManager(String email) throws Exception;

    void managerNominate(Object[] nominations);

    //List<Nominations> getAllNominations();
}
