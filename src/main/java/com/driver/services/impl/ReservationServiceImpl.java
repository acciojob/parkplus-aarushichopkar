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

        User user;
        ParkingLot parkingLot;
        //check if userId and parkingLotId exist
        try{
            user = userRepository3.findById(userId).get();
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        } catch (Exception e) {
            throw new Exception("Cannot make reservation");
        }


            //get list of all empty spaces that are same type or more
            List<Spot> spotList = parkingLot.getSpotList();
            List<Spot> unoccupiedSpots = spotList.stream()
                    .filter(spot -> Boolean.FALSE.equals(spot.getOccupied()))
                    .collect(Collectors.toList());

            List<SpotType> spotTypeAvail = new ArrayList<>();
            if (numberOfWheels <= 2) {
                spotTypeAvail.add(SpotType.TWO_WHEELER);
                spotTypeAvail.add(SpotType.FOUR_WHEELER);
                spotTypeAvail.add(SpotType.OTHERS);
            } else if (numberOfWheels <= 4) {
                spotTypeAvail.add(SpotType.FOUR_WHEELER);
                spotTypeAvail.add(SpotType.OTHERS);
            } else {
                spotTypeAvail.add(SpotType.OTHERS);
            }

            List<Spot> emptyAvailSpotList = new ArrayList<>();
            for (Spot s : unoccupiedSpots) {
                if (s.getSpotType() == SpotType.TWO_WHEELER && spotTypeAvail.contains(SpotType.TWO_WHEELER)) {
                    emptyAvailSpotList.add(s);
                } else if (s.getSpotType() == SpotType.FOUR_WHEELER && spotTypeAvail.contains(SpotType.FOUR_WHEELER)) {
                    emptyAvailSpotList.add(s);
                } else if (s.getSpotType() == SpotType.OTHERS && spotTypeAvail.contains(SpotType.FOUR_WHEELER)) {
                    emptyAvailSpotList.add(s);
                } else {
                    continue;
                }
            }

            Optional<Spot> opbestspt = emptyAvailSpotList.stream().min(Comparator.comparingInt(Spot::getPricePerHour));
            if (opbestspt.isPresent()) {
                Spot bestspt = opbestspt.get();;
                Reservation reservation = new Reservation();
                reservation.setNumberOfHours(reservation.getNumberOfHours());
                reservation.setSpot(bestspt);
                reservation.setUser(user);
                bestspt.setOccupied(true);
                bestspt.getReservationList().add(reservation);
                user.getReservationList().add(reservation);
                userRepository3.save(user);
                spotRepository3.save(bestspt);
                return reservation;
            } else {
                throw new Exception("Cannot make reservation");
            }

        }
    }
}
