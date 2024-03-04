package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot newParkingLot = new ParkingLot();
        newParkingLot.setName(name);
        newParkingLot.setAddress(address);
        List<Spot> l = new ArrayList<Spot>();
        newParkingLot.setSpotList(l);
        return parkingLotRepository1.save(newParkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        Spot newSpot = new Spot();

        Optional<ParkingLot> optionalParkingLot = parkingLotRepository1.findById(parkingLotId);

        if(!optionalParkingLot.isPresent()) {
            throw new RuntimeException("Invalis Parkinglot ID");
        }
        ParkingLot parkingLot = optionalParkingLot.get();
        newSpot.setPricePerHour(pricePerHour);
        newSpot.setOccupied(false);
        newSpot.setReservationList(new ArrayList<>());
        if(numberOfWheels<=2){
            newSpot.setSpotType(SpotType.TWO_WHEELER);
        } else if (numberOfWheels<=4) {
            newSpot.setSpotType(SpotType.FOUR_WHEELER);
        }
        else{
            newSpot.setSpotType(SpotType.OTHERS);
        }

        List<Spot> newSpotList = parkingLot.getSpotList();        //get spotlist of the parkinglot
        newSpotList.add(newSpot);                                               //add newSpot to the list
        parkingLot.setSpotList(newSpotList);                      //set this new list
        parkingLotRepository1.save(parkingLot);
        newSpot.setParkingLot(parkingLot);


        return newSpot;
    }

    @Override
    public void deleteSpot(int spotId) {
        Optional<Spot> optionalSpot = spotRepository1.findById(spotId);
        if(optionalSpot.isPresent()){

            Spot spot = optionalSpot.get();
            List<Spot> spotList = spot.getParkingLot().getSpotList();

            spotList.removeIf(s -> s.getId()==spotId);
            spot.getParkingLot().setSpotList(spotList);
            parkingLotRepository1.save(spot.getParkingLot());

        }
        spotRepository1.deleteById(spotId);

    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour){
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository1.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()){
            throw new RuntimeException("Invalid ParkingLot ID");
        }
        ParkingLot parkingLot = optionalParkingLot.get();

        Spot spot = null;
        for(Spot spt: parkingLot.getSpotList()){
            if(spt.getId() == spotId){
                spot = spt;
                break;
            }
        }
        if(spot==null){
            throw new RuntimeException("Invalid Spot ID");
        }
        spot.setPricePerHour(pricePerHour);

        return spotRepository1.save(spot);
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository1.findById(parkingLotId);
        if(optionalParkingLot.isPresent()){
            ParkingLot parkingLot = optionalParkingLot.get();
            List<Spot> spotList = parkingLot.getSpotList();
            for(Spot s: spotList){
                spotRepository1.deleteById(s.getId());
            }
            parkingLotRepository1.deleteById(parkingLotId);
        }
    }
}
