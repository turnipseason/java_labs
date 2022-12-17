import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class QueryDateTime
{
    LocalDateTime beginDateTime;
    LocalDateTime endDateTime;
    public void setBeginDateTime(LocalDateTime beginDateTime)
    {
        this.beginDateTime = beginDateTime;
    }
    public void setEndDateTime(LocalDateTime endDateTime)
    {
        this.endDateTime = endDateTime;
    }
    public long getQueryExecutionTime()
    {
        // если запрос выполнялся в пределе одного дня
        if(this.beginDateTime.getYear() == this.endDateTime.getYear() &&
                this.beginDateTime.getMonth() == this.endDateTime.getMonth() &&
                this.beginDateTime.getDayOfMonth() == this.endDateTime.getDayOfMonth())
        {
            LocalDateTime result = LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonth(),
                    endDateTime.getDayOfMonth(), endDateTime.getHour(), endDateTime.getMinute(),
                    endDateTime.getSecond());

            result = subtractBeginTimeFromDateTime(result);

            return TimeUnit.HOURS.toSeconds(result.getHour()) +
                    TimeUnit.MINUTES.toSeconds(result.getMinute()) +
                    result.getSecond();
        }
        //... в пределе одного месяца
        else if(this.beginDateTime.getYear() == this.endDateTime.getYear() &&
                this.beginDateTime.getMonth() == this.endDateTime.getMonth())
        {
            LocalDate resultDate = LocalDate.of(endDateTime.getYear(), endDateTime.getMonth(), endDateTime.getDayOfMonth());
            LocalTime resultTime = LocalTime.of(endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond());

            resultDate = resultDate.minusDays(this.beginDateTime.getDayOfMonth());

            resultTime = subtractBeginTimeFromTime(resultTime);


            return TimeUnit.DAYS.toSeconds(resultDate.getDayOfMonth()) -
                    TimeUnit.HOURS.toSeconds(resultTime.getHour()) -
                    TimeUnit.MINUTES.toSeconds(resultTime.getMinute()) -
                    resultTime.getSecond();
        }
        else
        {
            LocalDate resultDate = LocalDate.of(endDateTime.getYear(), endDateTime.getMonth(), endDateTime.getDayOfMonth());
            LocalTime resultTime = LocalTime.of(endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond());

            resultDate = subtractBeginDateFromEndDate(resultDate);
            resultTime = subtractBeginTimeFromTime(resultTime);

            return TimeUnit.DAYS.toSeconds(365 * resultDate.getYear()) +
                    TimeUnit.DAYS.toSeconds(30 * resultDate.getMonthValue()) +
                    TimeUnit.DAYS.toSeconds(resultDate.getDayOfMonth()) -
                    TimeUnit.HOURS.toSeconds(resultTime.getHour()) -
                    TimeUnit.MINUTES.toSeconds(resultTime.getMinute()) -
                    resultTime.getSecond();
        }
    }

    private LocalDate subtractBeginDateFromEndDate(LocalDate resultDate) {
        resultDate = resultDate.minusYears(this.beginDateTime.getYear());
        resultDate = resultDate.minusMonths(this.beginDateTime.getMonthValue());
        resultDate = resultDate.minusDays(this.beginDateTime.getDayOfMonth());
        return resultDate;
    }

    private LocalTime subtractBeginTimeFromTime(LocalTime resultTime) {
        resultTime = resultTime.minusHours(this.beginDateTime.getHour());
        resultTime = resultTime.minusMinutes(this.beginDateTime.getMinute());
        resultTime = resultTime.minusSeconds(this.beginDateTime.getSecond());
        return resultTime;
    }

    private LocalDateTime subtractBeginTimeFromDateTime(LocalDateTime result) {
        result = result.minusHours(this.beginDateTime.getHour());
        result = result.minusMinutes(this.beginDateTime.getMinute());
        result = result.minusSeconds(this.beginDateTime.getSecond());
        return result;
    }
}
