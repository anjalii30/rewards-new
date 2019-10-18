package com.rar.service.impl;

import com.rar.exception.InvalidUserException;
import com.rar.model.Evidences;
import com.rar.model.NominationPojo;
import com.rar.model.Nominations;
import com.rar.repository.EvidencesRepository;
import com.rar.repository.ManagerRepository;
import com.rar.repository.NominationsRepository;
import com.rar.service.NominationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

//import com.rar.utils.CheckDisable;


@Service
@Transactional
public class NominationsServiceImpl implements NominationsService {

    @Autowired
    NominationsRepository nominationsRepository;
    @Autowired
    EvidencesRepository evidencesRepository;

    @Autowired
    private ManagerRepository managerRepository;
    @Override
    public ResponseEntity<?> nominationSave(List<NominationPojo> nominationPojo) {

        Nominations nominations1 = new Nominations();
        List<HashMap<String, Object>> s = new ArrayList<>();
        for(int i=0;i<nominationPojo.size();i++) {
            Nominations nominations = new Nominations();

                nominations.setUserID(nominationPojo.get(i).getUserId());
                nominations.setRewardID(nominationPojo.get(i).getRewardId());
                nominations.setProject_name(nominationPojo.get(i).getProject_name());
                nominations.setSelected(nominationPojo.get(i).isSelected());
                nominations.setReward_name(nominationPojo.get(i).getReward_name());
                nominations.setEmployee_name(nominationPojo.get(i).getEmployee_name());
                nominations.setHr_selected(nominationPojo.get(i).isHr_selected());
                nominations.setReason(nominationPojo.get(i).getReason());

                nominationsRepository.save(nominations);

            long nominationID = nominations.getNominationID();



            Evidences evidences = new Evidences();
       //     System.out.println(nominationPojo.getEvidencesPojoList().size());

            for (int j = 0; j < nominationPojo.get(i).getEvidencesPojoList().size(); j++) {
                evidences = new Evidences();

                evidences.setNominationID(nominationID);
                System.out.println("test"+nominationID);
                evidences.setCriteria_desc(nominationPojo.get(i).getEvidencesPojoList().get(j).getCriteria_desc());
                evidences.setEvidences(nominationPojo.get(i).getEvidencesPojoList().get(j).getEvidences());
                evidences.setText_evidence(nominationPojo.get(i).getEvidencesPojoList().get(j).getText_evidence());

                evidencesRepository.save(evidences);
            }

//            s.get(i).put("evidences", evidences);
//            s.get(i).put("nominations", nominations);
//            Object returnValue = s;

           // return ResponseEntity.ok(s);
        }

       return ResponseEntity.ok(s);
    }

    @Override
    public List<Nominations> GetData(Long rewardID) throws Exception {
        

            List<Nominations> nominations = null;
//            Optional<Nominations> rId=nominationsRepository.findByRewardId(rewardID);
  //          if(rId.isPresent()) {
                nominations = nominationsRepository.GetData(rewardID);
                return nominations;
    //        }
     //       else
       //         throw new Exception("No nominations for this reward");

    }

    @Override
    public  List<List<Nominations>> showToManager(String manager_email,Long reward_id) throws Exception {

        try {
            Long manager_id = managerRepository.findByEmail(manager_email);
            Long[] members = nominationsRepository.getMembers(manager_id);

            List<List<Nominations>> getNominations = new ArrayList<>();

            for (int i = 0; i < members.length; i++) {
                if(nominationsRepository.getNominations((members[i]),reward_id).isEmpty())
                    continue;
                getNominations.add (nominationsRepository.getNominations(members[i],reward_id));
            }
            return getNominations;
        }catch (Exception e) {

            throw new InvalidUserException("you are not a manager");

        }
    }

    @Override
    public void awardeeSelect(Map<String, Long[]> nomination1_id) {

        Long[] nomination_id= nomination1_id.get("nomination_id");

        for(int i=0;i<nomination_id.length;i++){

            nominationsRepository.awardeeSelect(nomination_id[i]);
        }

    }

    @Override
    public List<Map<String,String>> getAwardedPeople() {
        return nominationsRepository.getAwarded();
    }

    @Override
    public List<List<Nominations>> showAllToManager(String email) throws Exception {
        try {
            System.out.println(email);
            Long manager_id = managerRepository.findByEmail(email);
            System.out.println(manager_id);
            Long[] members = nominationsRepository.getMembers(manager_id);
            System.out.println(Arrays.toString(members));
            List<List<Nominations>> getNominations = new ArrayList<>();
            ;
          //  List<Nominations> getNominations = null;
            for (int i = 0; i < members.length; i++) {
                System.out.println(members.length);
                System.out.println(members[i]);
               // getNominations = (nominationsRepository.getAllNominations(members[i]));
                System.out.println("test"+nominationsRepository.getAllNominations(members[i]));
                if(nominationsRepository.getAllNominations(members[i]).isEmpty())
                    continue;
                getNominations.add(nominationsRepository.getAllNominations(members[i]));
                System.out.println(getNominations);
            }
            System.out.println(getNominations);
            return getNominations;
        }catch (Exception e) {
             System.out.println(e);
            throw new InvalidUserException("you are not a manager");

        }
    }

//    @Override
//    public void managerNominate(List<NominationPojo> nominationsList) {
//
//        System.out.println(nominationsList.size());
//        Nominations nominations=new Nominations();
//        for(int i=0; i<nominationsList.size();i++) {
//            nominations.setUserID(nominationsList.);
//
//            System.out.println(nominationsList.get(i));
//
//        }
//    }

   @Override
    public List<Nominations> getAllNominations() {
        return nominationsRepository.getAllNominations();
 }

}
