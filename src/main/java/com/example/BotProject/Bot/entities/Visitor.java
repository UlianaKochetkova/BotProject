package com.example.BotProject.Bot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Класс-таблица на одобрение заявки
 * User - отправитель, chatid - куда отсылать, person - Данные заявки, статус???
 */
@Entity
public class Visitor {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    //Данные человека, который внес запись
    private String userFrom;
    //Chat-id
    //TODO: поменять тип на string и поменять весь код заново
    private Long chatIdFrom;

    //Согласие на обработку данных
    private boolean consent;
    //Способ заполнения данных
    private Integer way;

    //Данные в заявке
    //Из стандартного ввода
    private String first_name;
    private String last_name;
    private String middle_name;

    private String visit_date;
    private String purpose;

    //Из паспортного ввода
    private String series;
    private String number;
    private String place;
    private String passport_date;
    private String registration;
    private byte[] attachment;

    public String AttachmentExist(){
        if (getAttachment()!=null){
            return "получено";
        }
        return "отсутствует";
    }
    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }


    /**
     * Парсинг ФИО
     * @param fio
     */
    public void setFio(String fio){
        String lname=fio.substring(0,fio.indexOf(' ')).trim();
        fio=fio.substring(fio.indexOf(' ')+1);
        String fname=fio.substring(0,fio.indexOf(' ')).trim();
        String mname=fio.substring(fio.indexOf(' ')+1).trim();

        setFirst_name(fname);
        setLast_name(lname);
        setMiddle_name(mname);
    }



    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPassport_date() {
        return passport_date;
    }

    public void setPassport_date(String passport_date) {
        this.passport_date = passport_date;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

//    @CollectionTable(name="Person",joinColumns = @JoinColumn(name="person_id"))
//    private Person person;

    //Статус заявки??
    private String status;

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }

    public Integer getWay() {
        return way;
    }

    public void setWay(Integer way) {
        this.way = way;
    }




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public Long getChatIdFrom() {
        return chatIdFrom;
    }

    public void setChatIdFrom(Long chatIdFrom) {
        this.chatIdFrom = chatIdFrom;
    }

//    public Person getPerson() {
//        return person;
//    }
//
//    public void setPerson(Person person) {
//        this.person = person;
//    }



}
