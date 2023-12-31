package com.course.project.gamecenter.business;

import com.course.project.gamecenter.business.entity.GameAttachDataEntity;
import com.course.project.gamecenter.business.entity.GameAttackEntity;
import com.course.project.gamecenter.business.entity.GamePlayEntity;
import com.course.project.gamecenter.domain.events.producer.PlayModeUIProduce;
import com.course.project.gamecenter.domain.events.producer.UserScoreProducer;
import com.course.project.gamecenter.domain.repository.UserDataDAO;
import com.course.project.gamecenter.domain.repository.UserModeDAO;
import com.course.project.gamecenter.domain.repository.UserTrailDAO;
import com.course.project.gamecenter.dto.GameAttackDataReq;
import com.course.project.gamecenter.dto.GameAttackReq;
import com.course.project.gamecenter.dto.GamePlayReq;
import com.course.project.gamecenter.enums.AttackEnum;
import com.course.project.gamecenter.port.GameCenterService;
import com.course.project.gamecenter.utils.AttackUserAlgorithmUtils;
import com.course.project.gamecenter.utils.CalculateUserScoreAlgorithmUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class GameCenterManager implements GameCenterService {

    @Resource
    private UserTrailDAO userTrailDAO;
    @Resource
    private UserModeDAO userModeDAO;
    @Resource
    private UserDataDAO userDataDAO;
    @Resource
    private AttackUserAlgorithmUtils attackUserAlgorithmUtils;
    @Resource
    private CalculateUserScoreAlgorithmUtils calculateUserScoreAlgorithmUtils;
    @Resource
    private UserScoreProducer userScoreProducer;
    @Resource
    private PlayModeUIProduce playModeUIProduce;

    @Override
    public GamePlayEntity play(GamePlayReq req) {
        Long userId = req.getUserId();
        Integer attackId = req.getAttackId();
        Integer heartRate = userDataDAO.getUserHearRate(userId);
        // since heartrate can't be null, so set a default value to avoid throwing null exception
        if (heartRate == null) {
            heartRate = 100;
        }

        GameAttackDataReq gameAttackDataReq = GameAttackDataReq.builder()
                .userId(userId)
                .heartRate(heartRate)
                .build();

        GameAttachDataEntity gameAttachDataEntity = calculateScore(gameAttackDataReq);

        log.info("【Scenario303 - GameCenter】-【GameCenter】User start play end and calculate score, userId={},score={}",
                userId, gameAttachDataEntity.getScore());

        return GamePlayEntity.builder()
                .userId(userId)
                .attackId(attackId)
                .score(gameAttachDataEntity.getScore())
                .date(new Date())
                .build();
    }

    @Override
    public void addUserMode(Long userId, Integer userMode) {
        userModeDAO.addUserMode(userId, userMode);
    }

    @Override
    public void upsertUserTrailId(Long userId, Integer trailId) {
        log.info("【Scenario301 - GameCenter】-【GameCenter】update user and trailId relation to db, userId={},trailId={},", userId, trailId);

        userTrailDAO.upsertUserTrailId(userId, trailId);
    }

    @Override
    public GameAttackEntity generateAttackMode(GameAttackReq req) {
        Long userId = req.getUserId();
        Long longitude = req.getLongitude();
        Long latitude = req.getLatitude();
        Integer trackId = getUserTrailId(userId);

        if (userId == null) {
            return null;
        }
        Integer trailId = getUserTrailId(userId);
        if (trailId == null) {
            return null;
        }
        AttackEnum attackEnum = getAttachEnumByModeId(userId, longitude, latitude);
        log.info("【Scenario302 - GameCenter】-【GameCenter】generate play mode, userId={},playMode={}",
                userId, attackEnum.getDesc());

        GameAttackEntity gameAttackEntity = GameAttackEntity.builder()
                .userId(userId)
                .attackId(attackEnum.getId())
                .attackName(attackEnum.getDesc())
                .attackTime(new Date())
                .trailId(trackId)
                .build();

        //send play mode to user device
        if (!AttackEnum.DO_NOT_ATTACK.equals(attackEnum)) {
            log.info("【Scenario302 - GameCenter】-【GameCenter】send mq to user device and display in it, userId={},playMode={}",
                    userId, attackEnum.getDesc());

//            playModeUIProduce.sender(gameAttackEntity);
        }

        return gameAttackEntity;
    }

    @Override
    public GameAttachDataEntity calculateScore(GameAttackDataReq req) {
        Long userId = req.getUserId();
        Integer heartRate = req.getHeartRate();

        Integer score = calculateUserScoreAlgorithmUtils.calculateUserScore(userId, heartRate);

        userDataDAO.upsertScore(userId, score);

        GameAttachDataEntity gameAttachDataEntity = GameAttachDataEntity.builder().userId(userId).score(score).build();

        userScoreProducer.sender(gameAttachDataEntity);

        log.info("【Scenario303 - GameCenter】-【GameCenter】send user score to 【Challenge Center】, userId={},score={}",
                userId, gameAttachDataEntity.getScore());

        return gameAttachDataEntity;
    }

    public Integer getUserTrailId(Long userId) {
        if (userTrailDAO.containUserTrail(userId)) {
            return userTrailDAO.getTrailIdByUserId(userId);
        }
        return 123;
    }

    private AttackEnum getAttachEnumByModeId(Long userId, Long longitude, Long latitude) {
        Integer userMode = userModeDAO.getUserModeByUserId(userId);
        Integer trailId = userTrailDAO.getTrailIdByUserId(userId);
        List<Integer> trailModeList = userTrailDAO.getTrailListModeByTrailId(trailId);

        return attackUserAlgorithmUtils.getAttackMethod(userId, userMode, longitude, latitude, trailModeList);
    }
}