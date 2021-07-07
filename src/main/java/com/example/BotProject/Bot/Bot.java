package com.example.BotProject.Bot;

import com.example.BotProject.AppForBot.entities.User;
import com.example.BotProject.AppForBot.entities.UserRepository;
import com.example.BotProject.Bot.entities.Visitor;
import com.example.BotProject.Bot.entities.VisitorRepo;
import com.example.BotProject.Bot.entities.Wait;
import com.example.BotProject.Bot.entities.WaitRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    private String name;
    private String token;

    private VisitorRepo repo;
    private WaitRepo waitrepo;

//    public Bot(){
//        this.name="practice_first_bot";
//        this.token="1862052792:AAFB7CszZhg7BOlAW3ibM92LEPhegDfTzzI";
//    }

    public Bot(String name, String token, VisitorRepo vs, WaitRepo waitrepo){
        this.name=name;
        this.token=token;
        this.repo=vs;
        this.waitrepo=waitrepo;
    }

    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {

        try {
                execute(process(update));
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        //return name;
        return "practice_first_bot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        //return token;
        return "1862052792:AAFB7CszZhg7BOlAW3ibM92LEPhegDfTzzI";
    }


    //////////////////////////////////////////////////////////////////////


    /**
     * Метод, выбирающий способ обработки
     * Данные пересылает дальше
     * @param upd - сообщение с данными от пользователя
     * @return сообщение, которое нужно отправить
     */
    public SendMessage process(Update upd) throws TelegramApiException, IOException {
        //Если сообщение от пользователя содержит сообщение и оно содержит обычный текст
            if (upd.hasMessage() && upd.getMessage().hasText()){
                return textProcess(upd);
            }
            else if (upd.hasCallbackQuery()){
                return callbackProcess(upd);
            }
            //Обработка загрузки фото
            else if (upd.getMessage().hasPhoto() || upd.getMessage().hasDocument()){
                return photoProcess(upd);
            }

        return null;
    }

    /**
     * Обработка принятия фото/документа (скан паспорта)
     * @param upd
     * @return сообщение об успехе получения/ошибке ввода
     * @throws TelegramApiException
     * @throws IOException
     */
    public SendMessage photoProcess(Update upd) throws TelegramApiException, IOException {
        SendMessage sm=new SendMessage();
        if (upd.hasMessage() && (upd.getMessage().hasPhoto() || upd.getMessage().hasDocument())){
            Visitor vs=repo.findVisitorByChatIdFrom(upd.getMessage().getChatId());

            sm.setChatId(upd.getMessage().getChatId().toString());

            if (vs.getWay()==2){
                GetFile getFile = new GetFile();
                if (upd.getMessage().getDocument()!=null){
                    getFile.setFileId(upd.getMessage().getDocument().getFileId());
                }
                else
                    getFile.setFileId(upd.getMessage().getPhoto().get(0).getFileId());
                File f=execute(getFile);
                java.io.File file = downloadFile(f.getFilePath());
                vs.setAttachment(Files.readAllBytes(file.toPath()));
                sm.setText("Фото получено.");
                return confirm(sm,upd.getMessage().getChatId());
            }

        }
        sm.setText("Ошибка ввода");
        return sm;
    }

    /**
     * Обработка текстовых сообщений от пользователя
     * @param upd - введенное сообщение
     * @return ответ бота
     */
    public SendMessage textProcess(Update upd){
        //Создаем новый объект для ответа
        SendMessage sm = new SendMessage();

        sm.setChatId(upd.getMessage().getChatId().toString());

        //Извлекаем полученное сообщение
        String msg = upd.getMessage().getText();

        //обработка /start
        if (msg.equals("/start")) {
            String start_msg = "Соглашение об использовании персональных данных. Согласны?";
            sm.setText(start_msg);
            return inlineKey(sm);
        }

        //Переход на обработку по способам
        else {
            //Извлекаем посетителя по chatId
            Visitor vs=repo.findVisitorByChatIdFrom(upd.getMessage().getChatId());

            if (vs.getWay()==1){
                return standartInput(vs,msg,sm);
            }
            else if (vs.getWay()==2){
                return passwordInput(vs,msg,sm);
            }

        }
        return sm;
    }

    /**
     * Обработка нажатий на inline-keyboard
     * @param upd - введённое сообщение
     * @return ответ бота
     */
    public SendMessage callbackProcess(Update upd){
        //Создаем новый объект для ответа
        SendMessage sm = new SendMessage();

        //Устанавливаем чат, которому отвечаем
        sm.setChatId(upd.getCallbackQuery().getMessage().getChatId().toString());
        //Забираем код с кнопки
        String data = upd.getCallbackQuery().getData();

        //Обработка нажатий на кнопки
        switch(data){
            case "/start_yes":
            {
                //Заводим нового посетителя
                Visitor vs=new Visitor();
                vs.setConsent(true);
                vs.setChatIdFrom(upd.getCallbackQuery().getMessage().getChatId());
                vs.setUserFrom(upd.getCallbackQuery().getFrom().getId().toString());
                repo.save(vs);

                sm.setText("Ваше согласие принято"+"\n"+"Выберите способ ввода данных:");
                return inlineMethod(sm);
            }
            //break;
            case "/start_no":
            {
                sm.setText("Вы отказались от обработки данных. Работа невозможна");
            }
            break;

            //Обработка стандартного ввода
            case "/input1":
            {
                //Ищем запись в БД по id чата
                Visitor vs=repo.findVisitorByChatIdFrom(upd.getCallbackQuery().getMessage().getChatId());
                vs.setWay(1);
                repo.save(vs);
                sm.setText("Введите ФИО");
            }
            break;

            case "/input2":
            {
                //Ищем запись в БД по id чата
                Visitor vs=repo.findVisitorByChatIdFrom(upd.getCallbackQuery().getMessage().getChatId());
                vs.setWay(2);
                repo.save(vs);

                sm.setText("Введите ФИО");
            }
            break;

            //Обработка подтверждения заявки
            case "/confirm_yes":
            {
                //Ищем запись в БД по id чата
                Visitor vs=repo.findVisitorByChatIdFrom(upd.getCallbackQuery().getMessage().getChatId());
                //Добавляем данные в таблицу ожидания
                Wait w=new Wait();
                w.setChatIdFrom(upd.getCallbackQuery().getMessage().getChatId());
                w.setVisitor_id(vs.getId());
                waitrepo.save(w);
                //TODO: отправляем заявку на сервер
                sm.setText("Ваша заявка принята");

            }
            break;
            case "/confirm_no":
            {
                //Ищем запись в БД по id чата
                Visitor vs=repo.findVisitorByChatIdFrom(upd.getCallbackQuery().getMessage().getChatId());
                //Удаляем имеющуюся запись
                repo.delete(vs);
                sm.setText("Заявка обнулена");
            }
            break;
        }
        return sm;
    }

    /**
     * Метод, выдающий inline-клавиатуру для согласия на обработку данных
     * @param sm - сообщение, к которому добавляется inline-клавиатура
     * @return модифицированное сообщение, которое отправит бот
     */
    public synchronized SendMessage inlineKey(SendMessage sm) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        InlineKeyboardButton but1=new InlineKeyboardButton();
        but1.setText("Да");
        but1.setCallbackData("/start_yes");
        InlineKeyboardButton but2=new InlineKeyboardButton();
        but2.setText("Нет");
        but2.setCallbackData("/start_no");

        rowInline1.add(but1);
        rowInline1.add(but2);

        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        sm.setReplyMarkup(markupInline);
        return sm;
    }

    /**
     * Метод, выдающий inline-клавиатуру для подтверждения заявки
     * @param sm
     * @return
     */
    public synchronized SendMessage inlineConfirm(SendMessage sm) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        InlineKeyboardButton but1=new InlineKeyboardButton();
        but1.setText("Да");
        but1.setCallbackData("/confirm_yes");
        InlineKeyboardButton but2=new InlineKeyboardButton();
        but2.setText("Нет");
        but2.setCallbackData("/confirm_no");

        rowInline1.add(but1);
        rowInline1.add(but2);

        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        sm.setReplyMarkup(markupInline);
        return sm;
    }

    /**
     * Метод, выдающий inline-клавиатуру для выбора методов ввода данных
     * @param sm - сообщение, к которому добавляется inline-клавиатура
     * @return модифицированное сообщение, которое отправит бот
     */
    public synchronized SendMessage inlineMethod(SendMessage sm) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();

        InlineKeyboardButton but1=new InlineKeyboardButton();
        but1.setText("Ввод 1. Ручной");
        but1.setCallbackData("/input1");
        InlineKeyboardButton but2=new InlineKeyboardButton();
        but2.setText("Ввод 2. Паспортный");
        but2.setCallbackData("/input2");
        InlineKeyboardButton but3=new InlineKeyboardButton();
        but3.setText("Ввод 3.?");
        but3.setCallbackData("/input3");
        rowInline1.add(but1);
        rowInline2.add(but2);
        rowInline3.add(but3);

        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        sm.setReplyMarkup(markupInline);
        return sm;
    }

    /**
     * Метод, анализирующий данные для стандартного ввода
     * @param msg - текстовые данные
     * @param sm - сформированный SM
     * @return обновленный sm
     */
    public SendMessage standartInput(Visitor vs, String msg, SendMessage sm){
        if (vs.getFirst_name()==null){
            //Обработка ввода ФИО
                vs.setFio(msg);
                repo.save(vs);
                sm.setText("ФИО введено. Введите дату");
        }

        else if (vs.getVisit_date()==null){
            if (msg.matches("(0[1-9]|[1-2][0-9]|3[0-1]).(0[1-9]|1[0-2]).(2[0-9]{3})")){
                vs.setVisit_date(msg);
                repo.save(vs);
                sm.setText("Дата получена. Введите цель посещения");
            }
            else sm.setText("Некорректный ввод даты посещения");
        }

        else if (vs.getPurpose() == null) {
            vs.setPurpose(msg);
            repo.save(vs);
            sm.setText("Цель посещения получена");
            return confirm(sm,vs.getChatIdFrom());
        }

        else sm.setText("Ошибка ввода 1");

        return sm;
    }

    public SendMessage passwordInput(Visitor vs, String msg, SendMessage sm){
        msg=msg.trim();
        if (vs.getFirst_name()==null){
                vs.setFio(msg);
                repo.save(vs);
                sm.setText("ФИО введено. Введите серию паспорта");
        }

        else if (vs.getSeries()==null){
            if (msg.matches("\\d{4}")){
                vs.setSeries(msg);
                repo.save(vs);
                sm.setText("Серия введена. Введите номер паспорта");
            }
        }

        else if (vs.getNumber()==null){
            if (msg.matches("\\d+")){
                vs.setNumber(msg);
                repo.save(vs);
                sm.setText("Номер введен. Введите место выдачи паспорта");
            }
            else sm.setText("Ошибка ввода номера");
        }

        else if (vs.getPlace()==null){
            vs.setPlace(msg);
            repo.save(vs);
            sm.setText("Место выдачи паспорта введено. Введите дату выдачи паспорта");
        }

        else if (vs.getPassport_date()==null){
            if (msg.matches("(0[1-9]|[1-2][0-9]|3[0-1]).(0[1-9]|1[0-2]).([1-9][0-9]{3})")){
                vs.setPassport_date(msg);
                repo.save(vs);
                sm.setText("Дата выдачи паспорта получена. Прикрепите файл или фото паспорта");
            }
            else sm.setText("Некорректный ввод даты выдачи паспорта");
        }

        else sm.setText("Ошибка ввода 2");

        return sm;
    }

    public SendMessage randomInput(String msg, SendMessage sm){

        return sm;
    }

    public SendMessage confirm(SendMessage sm, Long chatId){
        //Выводим последнее сообщение метода
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        //Создаем сообщение-подтверждение
        SendMessage end_sm=new SendMessage();
        end_sm.setChatId(sm.getChatId());
        //Находим в базе запись, которую обрабатываем
        Visitor vs=repo.findVisitorByChatIdFrom(chatId);

        if (vs.getWay()==1){
            end_sm.setText("Метод 1\n" +
                    "Фамилия: " + vs.getLast_name()+"\n"+
                    "Имя: " +vs.getFirst_name()+"\n"+
                    "Отчество: " +vs.getMiddle_name()+"\n"+
                    "Дата посещения: " +vs.getVisit_date()+"\n"+
                    "Цель посещения: "+vs.getPurpose()+"\n");
        }
        else if (vs.getWay()==2){
            end_sm.setText("Метод 2\n" +
                    "Фамилия: "+vs.getLast_name()+"\n" +
                    "Имя: " +vs.getFirst_name()+"\n" +
                    "Отчество: " +vs.getMiddle_name()+"\n" +
                    "Серия паспорта: " +vs.getSeries()+"\n" +
                    "Номер паспорта: " +vs.getNumber()+"\n" +
                    "Место выдачи паспорта: " +vs.getPlace()+"\n" +
                    "Дата выдачи паспорта: " +vs.getPassport_date()+"\n" +
                    "Фото паспорта: "+vs.AttachmentExist()+"\n");
        }
        return inlineConfirm(end_sm);
    }

}
