package com.help.hyozason_backend.service.helpreward;

import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import com.help.hyozason_backend.etc.ResponseService;
import com.help.hyozason_backend.repository.helpreward.HelpRewardRepository;
import org.springframework.stereotype.Service;

@Service
public class HelpRewardService extends ResponseService {
    private HelpRewardRepository helpRewardRepository;
    private HelpRewardEntity helpRewardEntity;
    public HelpRewardService(HelpRewardRepository helpRewardRepository) {
        this.helpRewardRepository = helpRewardRepository;
        this.helpRewardEntity = new HelpRewardEntity();
    }

    public int grantRewards(int rating) {
        switch (rating) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 5;
            case 4:
                return 7;
            case 5:
                return 10;
            default:
                throw new IllegalArgumentException("잘못된 입력입니다.");
        }
    }

    public int updateRewards(String userEmail, int rating) {
        HelpRewardEntity helpRewardEntity = helpRewardRepository.findByUserEmail(userEmail);

        if (helpRewardEntity == null) {
            helpRewardEntity = new HelpRewardEntity();
            helpRewardEntity.setUserEmail(userEmail);
            helpRewardEntity.setRewardScore(0);
        }

        int rewards = grantRewards(rating);
        int updatedRewards = helpRewardEntity.getRewardScore() + rewards;

        helpRewardEntity.setRewardScore(updatedRewards);
        helpRewardRepository.save(helpRewardEntity);

        return updatedRewards;
    }
}
