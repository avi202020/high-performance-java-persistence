package com.vladmihalcea.book.hpjp.hibernate.type;

import com.vladmihalcea.book.hpjp.util.AbstractTest;
import org.junit.Test;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;

import static org.junit.Assert.assertEquals;

/**
 * @author Vlad Mihalcea
 */
public class AttributeConverterMonthDayTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
            AnnualSubscription.class
        };
    }

    @Test
    public void test() {
        doInJPA(entityManager -> {
            entityManager.persist(
                new AnnualSubscription()
                    .setId(1L)
                    .setPriceInCents(700)
                    .setPaymentDay(
                        MonthDay.of(Month.AUGUST, 17)
                    )
            );
        });

        doInJPA(entityManager -> {
            AnnualSubscription subscription = entityManager.find(AnnualSubscription.class, 1L);

            assertEquals(MonthDay.of(Month.AUGUST, 17), subscription.getPaymentDay());
        });
    }

    @Entity(name = "AnnualSubscription")
    @Table(name = "annual_subscription")
    public static class AnnualSubscription {

        @Id
        private Long id;

        private double priceInCents;

        @Column(name = "payment_day", columnDefinition = "date")
        @Convert(converter = MonthDayDateAttributeConverter.class)
        private MonthDay paymentDay;

        public Long getId() {
            return id;
        }

        public AnnualSubscription setId(Long id) {
            this.id = id;
            return this;
        }

        public double getPriceInCents() {
            return priceInCents;
        }

        public AnnualSubscription setPriceInCents(double priceInCents) {
            this.priceInCents = priceInCents;
            return this;
        }

        public MonthDay getPaymentDay() {
            return paymentDay;
        }

        public AnnualSubscription setPaymentDay(MonthDay paymentDay) {
            this.paymentDay = paymentDay;
            return this;
        }
    }

    public static class MonthDayDateAttributeConverter
        implements AttributeConverter<MonthDay, java.sql.Date> {

        @Override
        public java.sql.Date convertToDatabaseColumn(MonthDay monthDay) {
            return java.sql.Date.valueOf(
                monthDay.atYear(1)
            );
        }

        @Override
        public MonthDay convertToEntityAttribute(java.sql.Date date) {
            LocalDate localDate = date.toLocalDate();
            return MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
        }
    }
}
