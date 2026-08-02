import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class FleetManagerTest {

    @Test
    void addTruckMakesTruckFindableById() {
        FleetManager manager = new FleetManager();
        Truck truck = new Truck("TRK-101", "Freightliner");

        manager.addTruck(truck);

        assertSame(truck, manager.findTruckById("TRK-101"));
    }

    @Test
    void findTruckByIdIgnoresLetterCase() {
        FleetManager manager = new FleetManager();
        Truck truck = new Truck("TRK-202", "Kenworth");

        manager.addTruck(truck);

        assertSame(truck, manager.findTruckById("trk-202"));
    }

    @Test
    void findTruckByIdReturnsNullWhenTruckDoesNotExist() {
        FleetManager manager = new FleetManager();

        assertNull(manager.findTruckById("MISSING"));
    }
}