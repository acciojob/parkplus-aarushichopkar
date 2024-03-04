package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        //check if userId and parkingLotId exist
        Optional<User> optionalUser = userRepository3.findById(userId);
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);

        if(optionalParkingLot.isPresent() && optionalUser.isPresent()){

            //get list of all empty spaces that are same type or more
            List<Spot> spotList = optionalParkingLot.get().getSpotList();
            List<Spot> unoccupiedSpots = spotList.stream()
                    .filter(spot -> Boolean.FALSE.equals(spot.getOccupied()))
                    .collect(Collectors.toList());

            List<SpotType> spotTypeAvail = new ArrayList<>();
            if(numberOfWheels<=2) {
                spotTypeAvail.add(SpotType.TWO_WHEELER);
                spotTypeAvail.add(SpotType.FOUR_WHEELER);
                spotTypeAvail.add(SpotType.OTHERS);
            } else if (numberOfWheels<=4) {
                spotTypeAvail.add(SpotType.FOUR_WHEELER);
                spotTypeAvail.add(SpotType.OTHERS);
            }else {
                spotTypeAvail.add(SpotType.OTHERS);
            }

            List<Spot> emptyAvailSpotList = new ArrayList<>();
            for(Spot s: unoccupiedSpots){
                if(s.getSpotType()==SpotType.TWO_WHEELER && spotTypeAvail.contains(SpotType.TWO_WHEELER)){
                    emptyAvailSpotList.add(s);
                } else if (s.getSpotType()==SpotType.FOUR_WHEELER && spotTypeAvail.contains(SpotType.FOUR_WHEELER)) {
                    emptyAvailSpotList.add(s);
                } else if (s.getSpotType()==SpotType.OTHERS && spotTypeAvail.contains(SpotType.FOUR_WHEELER)) {
                    emptyAvailSpotList.add(s);
                }else {
                    continue;
                }
            }

            Optional<Spot> bestspt = emptyAvailSpotList.stream().min(Comparator.comparingInt(Spot::getPricePerHour));
            if(bestspt.isPresent()){
                Reservation reservation = new Reservation();
                reservation.setNumberOfHours(reservation.getNumberOfHours());
                reservation.setSpot(bestspt.get());
                reservation.setUser(optionalUser.get());
                //payment
                return reservationRepository3.save(reservation);
            }
            else {
                throw new Exception("Cannot make reservation");
            }

        }else{
            throw new NullPointerException();
        }
    }
}
