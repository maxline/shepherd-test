package com.paazl.service;

import com.paazl.data.Sheep;
import com.paazl.data.State;
import com.paazl.data.repositories.CurrentBalanceRepository;
import com.paazl.data.repositories.SheepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class ShepherdService {
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

	public SheepStatusesDto getSheepStatusses() {
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

    public String orderNewSheep(int nofSheepDesired) {
        // TODO Implement sheep ordering feature
    	// TODO Write unit tests

        return String.format("In total %s sheep were ordered and added to your flock!", 333);
    }
}