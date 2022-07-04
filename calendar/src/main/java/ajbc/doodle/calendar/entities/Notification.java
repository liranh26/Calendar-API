package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Notification {
  
 private int id;
 private LocalDateTime localDateTime;
 private String title;
 private String message;

}
