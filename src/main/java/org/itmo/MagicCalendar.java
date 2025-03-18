package org.itmo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MagicCalendar {
    // Перечисление типов встреч
    public enum MeetingType {
        WORK, PERSONAL
    }

    public static int counter = 1;

    class Meeting {
        int id;
        LocalTime start;
        LocalTime end;
        MeetingType type;

        public Meeting(LocalTime startTime, LocalTime endTime, MeetingType type) {
            this.id = counter++;
            this.start = startTime;
            this.end = endTime;
            this.type = type;
        }
    }

    Map<String, List<Meeting>> userMeeting = new HashMap<>();

    /**
     * Запланировать встречу для пользователя.
     *
     * @param user имя пользователя
     * @param time временной слот (например, "10:00")
     * @param type тип встречи (WORK или PERSONAL)
     * @return true, если встреча успешно запланирована, false если:
     * - в этот временной слот уже есть встреча, и правило замены не выполняется,
     * - лимит в 5 встреч в день уже достигнут.
     */
    public boolean scheduleMeeting(String user, String time, MeetingType type) {
        if (user == null) {
            throw new RuntimeException("uncorrect");
        }
        if (time == null) {
            throw new RuntimeException("uncorrect");
        }
        if (type == null) {
            throw new RuntimeException("uncorrect");
        }

        LocalTime startTime = getLocalTime(time);
        LocalTime endTime = startTime.plusHours(1);

        Meeting newMeeting = new Meeting(startTime, endTime, type);

        List<Meeting> list = userMeeting.get(user);
        if (list == null) {
            list = new ArrayList<>();
        }

        if (!isCorrect(list, newMeeting)) {
            return false;
        }

        if (list.size() == 5) {
            return false;
        }

        list.add(newMeeting);
        userMeeting.put(user, list);

        return true;
    }

    private boolean isCorrect(List<Meeting> list, Meeting pair) {
        AtomicReference<Integer> deleteId = new AtomicReference<>(0);
        boolean result = list.stream().noneMatch(meeting -> {
            if (meeting.end.isAfter(pair.start) && meeting.start.isBefore(pair.end)) {
                if (meeting.type == MeetingType.PERSONAL) {
                    return true;
                } else {
                    deleteId.set(meeting.id);
                    return false;
                }
            }
            return false;
        });
        if (!result & deleteId.get() != 0) {
            list.removeIf(meeting -> meeting.id == deleteId.get());
        }
        return result;
    }

    private static LocalTime getLocalTime(String time) {
        int hourValue = 0;
        String hour = time.substring(0, 2);
        if (hour.startsWith("0")) {
            hour = hour.substring(1, 2);
        }
        hourValue = Integer.parseInt(hour);

        if (hourValue < 0 || hourValue > 24) {
            throw new RuntimeException("uncorrect");
        }

        int minuteValue = 0;
        String minute = time.substring(2);
        if (minute.startsWith("0")) {
            minute = minute.substring(1, 2);
        }
        minuteValue = Integer.parseInt(minute);
        if (minuteValue < 0 || minuteValue > 60) {
            throw new RuntimeException("uncorrect");
        }

        return LocalTime.of(hourValue, minuteValue, 0);
    }

    /**
     * Получить список всех встреч пользователя.
     *
     * @param user имя пользователя
     * @return список временных слотов, на которые запланированы встречи.
     */
    public List<String> getMeetings(String user) {
        if (user == null) {
            throw new RuntimeException("uncorrect");
        }
        List<Meeting> list = userMeeting.get(user);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list.stream().map(meeting -> meeting.start.toString()).toList();
    }

    /**
     * Отменить встречу для пользователя по заданному времени.
     *
     * @param user имя пользователя
     * @param time временной слот, который нужно отменить.
     * @return true, если встреча была успешно отменена; false, если:
     * - встреча в указанное время отсутствует,
     * - встреча имеет тип PERSONAL (отменять можно только WORK встречу).
     */
    public boolean cancelMeeting(String user, String time) {
        if (user == null) {
            throw new RuntimeException("uncorrect");
        }
        List<Meeting> list = userMeeting.get(user);
        if (list == null) {
            return false;
        }
        return false;
    }
}
