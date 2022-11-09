package ml.empee.commandsManager.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class Tuple<T, K> {

  private final T first;
  private final K second;

}
