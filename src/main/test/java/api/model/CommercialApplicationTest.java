package api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommercialApplicationTest {

    @Test
    void constructor_andGetters_workCorrectly() {
        CommercialApplication app = new CommercialApplication(
                1, "Pharma Ltd", 12345, "test@email.com",
                "07123456789", "1 Street", "Director Name"
        );

        assertEquals(1, app.getId());
        assertEquals("Pharma Ltd", app.getCompany_name());
        assertEquals(12345, app.getReg_num());
        assertEquals("test@email.com", app.getEmail());
        assertEquals("07123456789", app.getPhone());
        assertEquals("1 Street", app.getAddress());
        assertEquals("Director Name", app.getDirector());
    }
}
