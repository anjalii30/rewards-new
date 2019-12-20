package com.rar.service.impl;

import com.rar.DTO.History;
import com.rar.DTO.NominationPojo;
import com.rar.DTO.UserNominationDetails;
import com.rar.model.Evidences;
import com.rar.model.Nominations;
import com.rar.model.Rewards;
import com.rar.repository.*;
import com.rar.service.NominationsService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rar.utils.Constants.PUBLISHED;

@Service
@Transactional
public class NominationsServiceImpl implements NominationsService {

    @Autowired
    private NominationsRepository nominationsRepository;
    @Autowired
    private EvidencesRepository evidencesRepository;
    @Autowired
    private RewardsRepository rewardsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private SendEmail sendEmail;
    @Autowired
    private NominationsService nominationsService;


    @Override
    public ResponseEntity<?> nominationSave(List<NominationPojo> nominationPojo, Long manager_id) {

        List<HashMap<String, Object>> s = new ArrayList<>();
        for(int i=0;i<nominationPojo.size();i++) {
            Nominations nominations = new Nominations();

                nominations.setUserID(nominationPojo.get(i).getUserId());
                nominations.setRewardID(nominationPojo.get(i).getRewardId());
                nominations.setSelected(nominationPojo.get(i).isSelected());
                nominations.setHrSelected(nominationPojo.get(i).isHrSelected());
                nominations.setReason(nominationPojo.get(i).getReason());
                nominations.setProjectName(projectRepository.getProjectName(nominationPojo.get(i).getProjectId()));
                nominations.setRewardName(rewardsRepository.getRewardName(nominationPojo.get(i).getRewardId()));
                nominations.setUserName(userRepository.getNameById(nominationPojo.get(i).getUserId()));
                nominations.setManagerId(manager_id);
                nominations.setProjectId(nominationPojo.get(i).getProjectId());

              /*  System.out.println("rewards"+nominationPojo.get(i).getRewardId());
                System.out.println("userid"+nominationPojo.get(i).getUserId());
                System.out.println("projetId"+nominationPojo.get(i).getProject_id());
               */ nominationsRepository.save(nominations);

            long nominationID = nominations.getNominationID();

            for (int j = 0; j < nominationPojo.get(i).getEvidencesPojoList().size(); j++) {
               Evidences evidences = new Evidences();

                evidences.setNominationID(nominationID);
                evidences.setCriteriaDesc(nominationPojo.get(i).getEvidencesPojoList().get(j).getCriteriaDesc());
                evidences.setEvidences(nominationPojo.get(i).getEvidencesPojoList().get(j).getEvidences());
                evidences.setTextEvidence(nominationPojo.get(i).getEvidencesPojoList().get(j).getTextEvidence());

                evidencesRepository.save(evidences);
            }

        }

       return ResponseEntity.ok(s);
    }


    @Override
    public ResponseEntity<List<Nominations>> GetData(Long rewardID) throws Exception {

        List<Nominations> nominations = null;

        nominations = nominationsRepository.GetData(rewardID);

        return new ResponseEntity<>(nominations,HttpStatus.OK);
    }

    @Override
    public void awardeeSelect(Map<String, Long[]> n1Id) throws IOException, MessagingException, TemplateException {

        Long[] nominationID= n1Id.get("nomination_id");

        String[] emails=userRepository.getAllEmails();

        for (int i = 0; i < nominationID.length; i++) {

            nominationsRepository.awardeeSelect(nominationID[i]);
            rewardsRepository.updateAwardStatus(PUBLISHED,nominationsRepository.getRewardId(nominationID[i]));


            for (int j = 0; j < emails.length; j++) {

                String name=userRepository.getName(emails[j]);
                String reward_name=nominationsRepository.getRewardName(nominationID[i]);
                String user_name=nominationsRepository.getUserName(nominationID[i]);
                String image =userRepository.getImage(nominationsRepository.userId(nominationID[i]));

                Map<String,Object> root = new HashMap();
                root.put("name",name );
                root.put("user_name", user_name);
                root.put("reward_name",reward_name);
                root.put("image",image);
                if(nominationsRepository.userId(nominationID[i])==userRepository.getIdByEmail(emails[j]))
                    sendEmail.sendEmailToWinner(root,emails[j],"You have been awarded");
                else
                sendEmail.sendEmailWithAttachment(root,emails[j], "Employee awarded for the reward");
            }
        }
        nominationsService.rewardCoins(nominationID);
    }

    @Override
    public ResponseEntity<List<Map<String,String>>> getAwardedPeople() {
        return new ResponseEntity<>(nominationsRepository.getAwarded(),HttpStatus.OK);
    }

   @Override
    public ResponseEntity<List<Nominations>> getAllNominations() {
        return new ResponseEntity<>(nominationsRepository.getAllNominations(),HttpStatus.OK);
 }

    @Override
    public void managerSelect(Nominations[] nominations,Long manager_id,String manager_name)  {

        for(int i=0;i<nominations.length;i++){
            Long nomination_id =nominations[i].getNominationID();
            String reason=nominations[i].getReason();
            boolean selected=nominations[i].isSelected();
            nominationsRepository.updateSelected(selected,reason,nomination_id,manager_id,manager_name);
        }
    }

    @Override
    public ResponseEntity<List<Rewards>> nominatedRewards() {
        return new ResponseEntity<>(rewardsRepository.nominatedRewards(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List> getTopAwardee() {
        return new ResponseEntity<>(nominationsRepository.getTopAwardee(), HttpStatus.OK);
    }

    @Override
    public void rewardCoins(Long[] nominationID) {

       int count = nominationID.length;
      // String rewardName = nominationsRepository.getRewardName(nomination_id[0]);
       //Long rewardId = rewardsRepository.getRewardIdByName(rewardName);
       Long rewardId=nominationsRepository.getRewardId(nominationID[0]);
       Long rewardCoinValue = rewardsRepository.getCoinValue(rewardId);
       Long wonCoinValue = rewardCoinValue/count;
       for(int i=0; i<nominationID.length;i++){
           Long user_id = nominationsRepository.userId(nominationID[i]);
           Long currentWalletBalance = userRepository.getWalletBalance(user_id);
           Long newWalletBalance = currentWalletBalance + wonCoinValue;
           userRepository.updateWalletBalance(newWalletBalance,user_id);
       }
    }

    @Override
    public ResponseEntity<List<History>> history(String email) throws Exception{
        long managerId=managerRepository.findByEmail(email);
        List<History> histories= new ArrayList<>();
          long[] rewardId= nominationsRepository.rewardId(managerId);
        for(int i=0; i< rewardId.length; i++){
            List<UserNominationDetails> userNominationDetailsList = new ArrayList<>();
                long[] userIds= nominationsRepository.userIds(managerId, rewardId[i]);
                for(int j=0; j< userIds.length; j++){
                    userNominationDetailsList.add(j, new UserNominationDetails(userIds[j],nominationsRepository.gettingReason(managerId,rewardId[i],userIds[j]),userRepository.getUserName(userIds[j]),nominationsRepository.gettingSelected(managerId,rewardId[i],userIds[j])));
                }
                histories.add(i,new History(nominationsRepository.rewardName(rewardId[i]),userNominationDetailsList));
        }

        return new ResponseEntity<>(histories,HttpStatus.OK);
    }
}
