package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Optional<Reservation> optionalReservation = reservationRepository2.findById(reservationId);
        Reservation reservation = optionalReservation.get();

        int bill = reservation.getSpot().getPricePerHour()*reservation.getNumberOfHours();
        if(amountSent<bill){
            throw new Exception("Insufficient Amount");
        }else{
            mode = mode.toUpperCase();
            if(mode.equals(PaymentMode.CARD.toString()) || mode.equals(PaymentMode.CASH.toString()) || mode.equals(PaymentMode.UPI.toString())){
                Payment payment = new Payment();
                payment.setPaymentCompleted(true);
                if ("CASH".equals(mode)) {
                    payment.setPaymentMode(PaymentMode.CASH);
                } else if ("CARD".equals(mode)) {
                    payment.setPaymentMode(PaymentMode.CARD);
                } else{
                    payment.setPaymentMode(PaymentMode.UPI);
                }
                payment.setReservation(reservation);
                reservationRepository2.save(reservation);
                paymentRepository2.save(payment);
                return payment;
            }
            else {
                throw new Exception("Payment mode not detected");
            }
        }
    }
}
