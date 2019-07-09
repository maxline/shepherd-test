package com.paazl.service;

import com.paazl.data.CurrentBalance;
import com.paazl.data.Sheep;
import com.paazl.data.State;
import com.paazl.data.repositories.CurrentBalanceRepository;
import com.paazl.data.repositories.SheepRepository;
import com.paazl.util.CurrentBalanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

import static com.paazl.common.Constants.*;

@Service
public class ShepherdService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SheepRepository sheepRepository;
    private CurrentBalanceRepository currentBalanceRepository;

    @SuppressWarnings("unused")
    private Integer priceOfSheep;

    @Autowired
    public ShepherdService(
            SheepRepository sheepRepository,
            CurrentBalanceRepository currentBalanceRepository,
            @Value("${price_of_new_sheep}") Integer priceOfSheep) {
        this.sheepRepository = sheepRepository;
        this.currentBalanceRepository = currentBalanceRepository;
        this.priceOfSheep = priceOfSheep;
    }

    public SheepStatusesDto getSheepStatuses() {
        List<Sheep> healthySheep = sheepRepository.findAllByState(State.HEALTHY);
        List<Sheep> deadSheep = sheepRepository.findAllByState(State.DEAD);

        return new SheepStatusesDto(
                healthySheep.size(),
                deadSheep.size()
        );
    }

    public BigInteger getBalance() {
        return currentBalanceRepository.findFirstByOrderByTimestampDesc().getBalance();
    }

    @Transactional
    public String orderNewSheep(int nofSheepDesired) {
        // TODO Implement sheep ordering feature
        // TODO Write unit tests

        CurrentBalance currentBalance = getCurrentBalance();
        BigInteger orderTotal = BigInteger.valueOf(nofSheepDesired * priceOfSheep);

        try {
            validateBalance(currentBalance, orderTotal);

            updateSheep(nofSheepDesired);
            updateBalance(currentBalance, orderTotal);

            String successMessage = String.format(SUCCESS_MESSAGE_SHEEPS_WERE_ORDERED, nofSheepDesired);
            log.info(successMessage);
            return successMessage;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void validateBalance(CurrentBalance currentBalance, BigInteger orderTotal) throws Exception {
        if (currentBalance == null) {
            String errorMessage = ERROR_MESSAGE_UNABLE_GET_BALANCE;
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        if (isNotEnoughMoney(currentBalance, orderTotal)) {
            String errorMessage = String.format(ERROR_MESSAGE_NOT_ENOUGH_MONEY,
                    orderTotal, currentBalance.getBalance());
            log.info(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    private boolean isNotEnoughMoney(CurrentBalance currentBalance, BigInteger orderCost) {
        return currentBalance.getBalance().compareTo(orderCost) < 0;
    }

    private CurrentBalance getCurrentBalance() {
        return currentBalanceRepository.findFirstByOrderByTimestampDesc();
    }

    private void updateBalance(CurrentBalance currentBalance, BigInteger orderTotal) {
        currentBalanceRepository.save(CurrentBalanceUtils.subtractBalance(currentBalance, orderTotal));
    }

    private void updateSheep(int nofSheepDesired) {
        IntStream.range(0, nofSheepDesired).forEach($ -> sheepRepository.save(new Sheep()));
    }

    public void setPriceOfSheep(Integer priceOfSheep) {
        this.priceOfSheep = priceOfSheep;
    }
}