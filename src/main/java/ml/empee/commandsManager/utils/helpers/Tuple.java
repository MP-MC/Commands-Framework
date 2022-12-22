package ml.empee.commandsManager.utils.helpers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@Getter
public class Tuple<T, K> {

  private final T first;
  private final K second;

}
