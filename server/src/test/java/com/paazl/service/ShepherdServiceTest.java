package com.paazl.service;

import com.paazl.data.CurrentBalance;
import com.paazl.data.Sheep;
import com.paazl.data.repositories.CurrentBalanceRepository;
import com.paazl.data.repositories.SheepRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;

import static com.paazl.common.Constants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ShepherdServiceTest {

    private static final int PRICE_OF_SHEEP = 500;
    @Mock
    private SheepRepository sheepRepository;
    @Mock
    private CurrentBalanceRepository currentBalanceRepository;

    @InjectMocks
    private ShepherdService testInstance;

    @Before
    public void setup() {
        testInstance.setPriceOfSheep(PRICE_OF_SHEEP);
    }

    @Test
    public void orderNewSheep_ShouldReturnErrorMessageUnableGetBalance_WhenBalanceIsNull() {

        when(currentBalanceRepository.findFirstByOrderByTimestampDesc()).thenReturn(null);
        assertThat(testInstance.orderNewSheep(1), is(ERROR_MESSAGE_UNABLE_GET_BALANCE));

        verify(sheepRepository, never()).save(any(Sheep.class));
        verify(currentBalanceRepository, never()).save(any(CurrentBalance.class));
    }

    @Test
    public void orderNewSheep_ShouldReturnErrorMessageNotEnoughMoney_WhenBalanceIsNotEnough() {
        BigInteger currentBalance = new BigInteger("499");
        int nofSheepDesired = 1;
        int orderTotal = PRICE_OF_SHEEP * nofSheepDesired;

        when(currentBalanceRepository.findFirstByOrderByTimestampDesc()).thenReturn(new CurrentBalance(currentBalance));

        assertThat(testInstance.orderNewSheep(nofSheepDesired),
                is(String.format(ERROR_MESSAGE_NOT_ENOUGH_MONEY, orderTotal, currentBalance)));

        verify(sheepRepository, never()).save(any(Sheep.class));
        verify(currentBalanceRepository, never()).save(any(CurrentBalance.class));
    }

    @Test
    public void orderNewSheep_ShouldReturnSuccessMessage_WhenBalanceIsEnough() {
        BigInteger currentBalance = new BigInteger("1504");
        int nofSheepDesired = 3;

        when(currentBalanceRepository.findFirstByOrderByTimestampDesc()).thenReturn(new CurrentBalance(currentBalance));

        assertThat(testInstance.orderNewSheep(nofSheepDesired),
                is(String.format(SUCCESS_MESSAGE_SHEEPS_WERE_ORDERED, nofSheepDesired)));

        verify(sheepRepository, Mockito.times(nofSheepDesired)).save(any(Sheep.class));

        ArgumentCaptor<CurrentBalance> argument = ArgumentCaptor.forClass(CurrentBalance.class);
        verify(currentBalanceRepository, times(1)).save(argument.capture());
        assertThat(argument.getValue().getBalance(), is(new BigInteger("4")));
    }
}