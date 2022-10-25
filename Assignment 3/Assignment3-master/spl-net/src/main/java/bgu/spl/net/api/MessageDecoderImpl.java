package bgu.spl.net.api;

import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.messages.*;
import bgu.spl.net.srv.messages.Error;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;

public class MessageDecoderImpl {

    private int len = 0;
    private Message message;
    private Object[] template;
    private int index = 0;
    private ShortEncDec bytesToShort = new ShortEncDec();
    private CharEncDec bytesToChar = new CharEncDec();
    private StringEncDec bytesToString = new StringEncDec();

    public Message decodeNextByte(byte nextByte) {
        if (len < 2) {
            Short opcode = bytesToShort.decodeNextByte(nextByte); //the argument will hold the opcode after 2 bytes according to this method
            if (opcode != null) {
                bytesToShort = new ShortEncDec(); // resets the encoder back, so it will be ready for the next message
                createMessage(opcode);
                createTemplate(message);
            }
        }

        if ((len > 1) && (template != null) && (index < template.length)) {//checks we are not done with all the fields
            if (template[index].getClass() == Byte.class) {
                template[index] = nextByte;
                index++;
            } else if (template[index].getClass() == Short.class) {
                Short toInsert = bytesToShort.decodeNextByte(nextByte);
                if (toInsert != null) {
                    bytesToShort = new ShortEncDec(); // resets the encoder back, so it will be ready for the next message
                    template[index] = toInsert;
                    index++;
                }
            } else if (template[index].getClass() == Character.class) {
                Character toInsert = bytesToChar.decodeNextByte(nextByte);
                if (toInsert != null) {
                    bytesToChar = new CharEncDec(); // resets the encoder back, so it will be ready for the next message
                    template[index] = toInsert;
                    index++;
                }
            } else if (template[index].getClass() == String.class) {
                String toInsert = bytesToString.decodeNextByte(nextByte);
                if (toInsert != null && !toInsert.isEmpty()) {
                    bytesToString = new StringEncDec(); // resets the encoder back
                    template[index] = toInsert;
                    index++;
                    if (message.getClass() == PMReq.class && index == 2){
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        template[index] = " "+formatter.format(date).toString().substring(0,16);
                        index++;
                    }
                }

            }
        }
        if ((template != null) && (index == template.length)) { //meaning we are done with the template
            try {
                Message message = messageFromTemplate();
                template = null;
                index = 0;
                len = 0;
                return message;


            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        len++;
        return null;
    }

    private void createMessage(short opcode) {
        switch (opcode) {
            case 1:
                message = new RegisterReq();
                break;

            case 2:
                message = new LoginReq();
                break;

            case 3:
                message = new LogoutReq();
                break;

            case 4:
                message = new FollowReq();
                break;

            case 5:
                message = new PostReq();
                break;
            case 6:
                message = new PMReq();
                break;

            case 7:
                message = new LogStatReq();
                break;

            case 8:
                message = new StatsReq();
                break;

            case 12:
                message = new Block();
                break;

        }
    }

    private void createTemplate(Message message) {
        short opcode = message.getOpcode();
        switch (opcode) {
            case 1:
                template = ((RegisterReq) message).getTemplate();
                break;
            case 2:
                template = ((LoginReq) message).getTemplate();
                break;
            case 3:
                template = message.getTemplate();
                break;
            case 4:
                template = message.getTemplate();
                break;
            case 5:
                template = ((PostReq) message).getTemplate();
                break;
            case 6:
                template = ((PMReq) message).getTemplate();
                break;
            case 7:
                template = message.getTemplate();
                break;
            case 8:
                template = ((StatsReq) message).getTemplate();
                break;
            case 12:
                template = message.getTemplate();
                break;
        }
    }

    private Message messageFromTemplate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class[] arr = new Class[template.length];
        for( int i=0; i<template.length; i++ ) {
            arr[i] = template[i].getClass();
        }
        return message.getClass().getConstructor(arr).newInstance(template);
    }
}



