import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import saps.archiver.core.*;

public class ArchiverTest {

    Archiver archiver;

    @Mock
    PermanentStorage permanentStorage;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}