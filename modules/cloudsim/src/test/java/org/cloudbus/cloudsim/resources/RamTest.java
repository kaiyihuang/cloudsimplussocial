package org.cloudbus.cloudsim.resources;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the class Ram.
 * Currently, testing this class is enough to test
 all the related classes ResourceBw, ResourceCpu, etc,
 once that these classes extend Ram
 but in fact don't include any new attribute or method.
 See the documentation of the class {@link Ram} for
 * more details.
 * @author Manoel Campos da Silva Filho
 */
public class RamTest {
    private static final Integer ZERO = 0;
    private static final Integer CAPACITY = 1000;
    private static final Integer DOUBLE_CAPACITY = CAPACITY*2;

    private static final Integer HALF_CAPACITY = CAPACITY/2;
    private static final Integer QUARTER_OF_CAPACITY = CAPACITY/4;    
    private static final Integer THREE_QUARTERS_OF_CAPACITY = QUARTER_OF_CAPACITY * 3;        
    
    private Ram createResource() {
        Ram instance = new Ram(CAPACITY);
        return instance;
    }

    @Test
    public void testIsFull() {
        Ram ram = createResource();
        assertFalse(ram.isFull());
        ram.allocateResource(HALF_CAPACITY);
        assertFalse(ram.isFull());
        ram.allocateResource(HALF_CAPACITY);
        assertTrue(ram.isFull());        
    }
    
    @Test
    public void testGetAndSetCapacityPriorToAllocateResource() {
        System.out.println("testGetAndSetCapacityPriorToAllocateResource");
        Integer expResult = CAPACITY;
        final Ram instance = createResource();
        Integer result = instance.getCapacity();
        assertEquals(expResult, result);
        
        expResult = HALF_CAPACITY;
        instance.setCapacity(expResult);
        result = instance.getCapacity();
        assertEquals(expResult, result);        
        try{
            instance.setCapacity(null);
            fail("An exception should be raised when traing to set the resource capacity to null.");
        }catch(Exception e){
        }

        expResult = DOUBLE_CAPACITY;
        instance.setCapacity(expResult);
        result = instance.getCapacity();
        assertEquals(expResult, result);        
    }

    @Test
    public void testGetAndSetCapacityAfterToAllocateResource() {
        System.out.println("testGetAndSetCapacityAfterToAllocateResource");
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getCapacity());
        
        //reduce the capacity
        Integer newCapacity = HALF_CAPACITY;
        boolean result = instance.setCapacity(newCapacity);
        assertTrue(result);
        
        //try an invalid capacity
        newCapacity = -1;
        result = instance.setCapacity(newCapacity);
        assertFalse(result);
        
        //try an invalid capacity
        newCapacity = 0;
        result = instance.setCapacity(newCapacity);
        assertFalse(result);
        
        //restore the original capacity
        newCapacity = CAPACITY;
        result = instance.setCapacity(newCapacity);
        assertTrue(result);

        //allocate resource and try to reduce the capacity below to the amount allocated
        final Integer allocated = HALF_CAPACITY;
        instance.allocateResource(allocated);
        assertEquals(allocated, instance.getAllocatedResource());
        newCapacity = QUARTER_OF_CAPACITY;
        result = instance.setCapacity(newCapacity);
        assertFalse(result);        
        
        //try to increase the resource capacity
        newCapacity = DOUBLE_CAPACITY;
        result = instance.setCapacity(newCapacity);
        assertTrue(result);     
    }

    @Test
    public void testAllocateResourceInvalidAllocations() {
        System.out.println("testAllocateResourceInvalidAllocations");
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getCapacity());
        Integer allocation = 0;
        
        //try allocated an invalid amount
        boolean result = instance.allocateResource(0);
        assertFalse(result);                        
        assertEquals(allocation, instance.getAllocatedResource());
        
        //try allocated an invalid amount
        result = instance.allocateResource(-1);
        assertFalse(result);
        assertEquals(allocation, instance.getAllocatedResource());

        //try allocated more than the total capacity
        result = instance.allocateResource(DOUBLE_CAPACITY);
        assertFalse(result);                        
        assertEquals(allocation, instance.getAllocatedResource());
    }

    @Test
    public void testAllocateResourceMultipleAllocations1() {
        System.out.println("testAllocateResourceMultipleAllocations1");
        final Ram instance = createResource();
        Integer allocation = 0, totalAllocation = 0;
        assertEquals(allocation, instance.getAllocatedResource());
        
        //allocate a valid amount
        allocation = THREE_QUARTERS_OF_CAPACITY;
        totalAllocation += allocation;
        boolean result = instance.allocateResource(allocation);
        assertTrue(result);                        
        assertEquals(totalAllocation, instance.getAllocatedResource());
        assertEquals(CAPACITY, instance.getCapacity());

        //try to allocate an amount not available anymore
        allocation = THREE_QUARTERS_OF_CAPACITY;
        result = instance.allocateResource(allocation);
        assertFalse(result);    
        //the allocated amount has keep unchanged, with the same value of the first allocation
        assertEquals(totalAllocation, instance.getAllocatedResource());
        
        //try to allocate an available amount
        allocation = QUARTER_OF_CAPACITY;
        totalAllocation += allocation;
        result = instance.allocateResource(allocation);
        assertTrue(result);  
        assertEquals(totalAllocation, instance.getAllocatedResource());        
        
        //try to allocate an amount not available anymore
        allocation = QUARTER_OF_CAPACITY;
        result = instance.allocateResource(allocation);
        assertFalse(result);    
        //the allocated amount has keep unchanged, with the same value of the first allocation
        assertEquals(totalAllocation, instance.getAllocatedResource());
    }
    
    @Test
    public void testAllocateResourceMultipleAllocations2() {
        System.out.println("testAllocateResourceMultipleAllocations2");
        final Ram instance = createResource();
        Integer totalAllocation = ZERO, totalAvailable = CAPACITY;
        assertEquals(CAPACITY, instance.getCapacity());
        assertEquals(totalAvailable, instance.getAvailableResource());
        assertEquals(ZERO, instance.getAllocatedResource());
        
        final Integer allocation = QUARTER_OF_CAPACITY;
        for(int i = 1; i <= 4; i++){
            //checks the available and allocated amount before allocation
            assertEquals(totalAvailable, instance.getAvailableResource());
            assertEquals(totalAllocation, instance.getAllocatedResource());

            //allocate
            boolean result = instance.allocateResource(allocation);
            assertTrue(result);                        
            
            //checks the available and allocated amount after allocation            
            totalAvailable -= allocation;
            totalAllocation += allocation;
            assertEquals(totalAvailable, instance.getAvailableResource());
            assertEquals(totalAllocation, instance.getAllocatedResource());
        }
        assertEquals(ZERO, instance.getAvailableResource());
        assertEquals(instance.getCapacity(), instance.getAllocatedResource());
        
        boolean result = instance.allocateResource(allocation);
        assertEquals(false, result);                
        //available and allocated amount has to be unchanged
        assertEquals(ZERO, instance.getAvailableResource());
        assertEquals(instance.getCapacity(), instance.getAllocatedResource());
        
        //increase the capacity
        final Integer oldCapacity = CAPACITY, newCapacity = oldCapacity + allocation;
        result = instance.setCapacity(newCapacity);
        assertEquals(true, result);                
        assertEquals(newCapacity, instance.getCapacity());  
        assertEquals(allocation, instance.getAvailableResource());
        assertEquals(oldCapacity, instance.getAllocatedResource());
        
        //try a new allocation
        result = instance.allocateResource(allocation);
        assertEquals(true, result);                
        assertEquals(ZERO, instance.getAvailableResource());
        assertEquals(newCapacity, instance.getAllocatedResource());                
    }    
    
    @Test
    public void testCapacityHasBeenSet() {
        System.out.println("capacityHasBeenSet");
        final Ram instance = createResource();
        boolean result = instance.capacityHasBeenSet();
        assertTrue(result);
        
        instance.setCapacity(DOUBLE_CAPACITY);
        result = instance.capacityHasBeenSet();
        assertTrue(result);        
    }

    @Test
    public void testSetAvailableResource() {
        System.out.println("setAvailableResource");
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getCapacity());
        assertEquals(CAPACITY, instance.getAvailableResource());
        assertEquals(ZERO, instance.getAllocatedResource());
        
        //try an invalid amount of available resource
        Integer availableResource = -1;
        assertFalse(instance.setAvailableResource(availableResource));
        assertEquals(CAPACITY, instance.getAvailableResource());
        
        /*Try to free more than the actual capacity*/
        availableResource = DOUBLE_CAPACITY;
        assertFalse(instance.setAvailableResource(availableResource));
        assertEquals(CAPACITY, instance.getAvailableResource());
        
        //no available resource
        availableResource = 0;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getAvailableResource());
        
        //all resource available 
        availableResource = CAPACITY;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getAvailableResource());

        //Half of the capacity freely available
        availableResource = HALF_CAPACITY;
        boolean result = instance.setAvailableResource(availableResource);
        assertTrue(result);
        assertEquals(availableResource, instance.getAvailableResource());
        assertEquals(availableResource, instance.getAllocatedResource());
        assertFalse(instance.setAvailableResource(-1));
    }

    @Test
    public void testGetAllocatedResource() {
        System.out.println("getAllocatedResource");
        final Ram instance = createResource();
        Integer expResult = 0;
        Integer result = instance.getAllocatedResource();
        assertEquals(expResult, result);

        expResult = HALF_CAPACITY;
        instance.allocateResource(expResult);
        result = instance.getAllocatedResource();
        assertEquals(expResult, result);

        // try to allocate 0 has to fail and the allocated resource cannot be changed
        Integer amountToAllocate = 0;
        assertFalse(instance.allocateResource(amountToAllocate));
        result = instance.getAllocatedResource();
        assertEquals(expResult, result);
        
        // try to allocate more than available has to fail and the allocated resource cannot be changed
        amountToAllocate = CAPACITY;
        assertFalse(instance.allocateResource(amountToAllocate));
        result = instance.getAllocatedResource();
        assertEquals(expResult, result);
        
    }
    
    @Test
    public void testSetAllocatedResource() {
        System.out.println("setAllocatedResource");
        final Ram instance = createResource();
        assertEquals(ZERO, instance.getAllocatedResource());
        
        Integer newTotalAllocatedResource = HALF_CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getAllocatedResource());
        
        newTotalAllocatedResource = QUARTER_OF_CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getAllocatedResource());
        
        newTotalAllocatedResource = THREE_QUARTERS_OF_CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getAllocatedResource());
        
        newTotalAllocatedResource = CAPACITY;
        final Integer expResult = CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(expResult, instance.getAllocatedResource());
        
        //it has to fail when trying to allocate more than the capacity
        //the allocated capacity has to stay unchanged
        newTotalAllocatedResource = DOUBLE_CAPACITY;
        assertFalse(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(expResult, instance.getAllocatedResource());
    }

    @Test
    public void testDeallocateResource() {
        System.out.println("deallocateResource");
        final Ram instance = createResource();
        assertEquals(ZERO, instance.getAllocatedResource());
        
        //try invalid values
        Integer amountToDeallocate = -1;
        assertFalse(instance.deallocateResource(amountToDeallocate));        
        amountToDeallocate = 0;
        assertFalse(instance.deallocateResource(amountToDeallocate));
        
        //there is nothing to deallocate
        amountToDeallocate = HALF_CAPACITY;
        assertFalse(instance.deallocateResource(amountToDeallocate));
        
        assertTrue(instance.allocateResource(CAPACITY));
        assertTrue(instance.deallocateResource(HALF_CAPACITY));
        assertTrue(instance.deallocateResource(HALF_CAPACITY));
        
        final Integer allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertEquals(allocated, instance.getAllocatedResource());
        assertTrue(instance.deallocateResource(allocated));
        assertFalse(instance.deallocateResource(allocated));
    }

    @Test
    public void testSumAvailableResource() {
        System.out.println("sumAvailableResource");
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getAvailableResource());

        //try not change the amount of available resource
        assertTrue(instance.sumAvailableResource(0));
        assertEquals(CAPACITY, instance.getAvailableResource());

        //decrease available resource (use of a negative value)
        Integer amountToSum = -QUARTER_OF_CAPACITY;
        Integer expResult = THREE_QUARTERS_OF_CAPACITY;
        assertTrue(instance.sumAvailableResource(amountToSum));
        assertEquals(expResult, instance.getAvailableResource());
        
        //try decreasing more than the capacity
        amountToSum = -DOUBLE_CAPACITY;
        assertFalse(instance.sumAvailableResource(amountToSum));
        assertEquals(expResult, instance.getAvailableResource());

        //use all resource (there will be no available amount)
        instance.deallocateAllResources();
        assertTrue(instance.allocateResource(CAPACITY));

        //increase available resource
        amountToSum = QUARTER_OF_CAPACITY;
        int totalAvailable = 0;
        for(int i = 1; i <= 4; i++) {
            totalAvailable += amountToSum;
            assertTrue(instance.sumAvailableResource(amountToSum));
            assertEquals((Integer)totalAvailable, instance.getAvailableResource());
        }
        
        assertFalse(instance.sumAvailableResource(amountToSum));
        assertEquals(CAPACITY, instance.getAvailableResource());
        
    }

    @Test
    public void testDeallocateAllResources() {
        System.out.println("deallocateAllResources");
        final Ram instance = createResource();
        Integer deallocated = ZERO;
        assertEquals(deallocated, instance.getAllocatedResource());
        assertEquals(deallocated, instance.deallocateAllResources());
        
        final Integer allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertTrue(instance.allocateResource(allocated));
        assertEquals(CAPACITY, instance.deallocateAllResources());
        assertEquals(ZERO, instance.deallocateAllResources());
    }

    @Test
    public void testIsResourceAmountAvailable() {
        System.out.println("isResourceAmountAvailable");
        final Ram instance = createResource();        
        assertTrue(instance.isResourceAmountAvailable(HALF_CAPACITY));
        assertTrue(instance.isResourceAmountAvailable(CAPACITY));

        Integer allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertTrue(instance.isResourceAmountAvailable(allocated));
        
        allocated = QUARTER_OF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertTrue(instance.isResourceAmountAvailable(allocated));
        assertFalse(instance.isResourceAmountAvailable(HALF_CAPACITY));
    }

    @Test
    public void testIsResourceAmountBeingUsed() {
        System.out.println("isResourceAmountBeingUsed");
        final Ram instance = createResource();
        assertEquals(ZERO, instance.getAllocatedResource());
        assertTrue(instance.allocateResource(HALF_CAPACITY));
        assertTrue(instance.isResourceAmountBeingUsed(QUARTER_OF_CAPACITY));
        assertTrue(instance.isResourceAmountBeingUsed(HALF_CAPACITY));
        assertFalse(instance.isResourceAmountBeingUsed(THREE_QUARTERS_OF_CAPACITY));
        assertFalse(instance.isResourceAmountBeingUsed(CAPACITY));
    }

    public void testIsSuitable() {
        System.out.println("isSuitable");
        final Ram instance = createResource();
        assertEquals(ZERO, instance.getAllocatedResource());
        final Integer allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertEquals(allocated, instance.getAllocatedResource());
        
        assertFalse(instance.isSuitable(CAPACITY));
        
        //if no new allocations is done, the request amount has be stay always availalbe
        assertTrue(instance.isSuitable(HALF_CAPACITY));
        assertTrue(instance.isSuitable(HALF_CAPACITY));
        assertTrue(instance.isSuitable(HALF_CAPACITY));
        assertTrue(instance.isSuitable(HALF_CAPACITY));
        
        assertTrue(instance.isSuitable(THREE_QUARTERS_OF_CAPACITY));
        
        //allocate more resources
        assertTrue(instance.allocateResource(QUARTER_OF_CAPACITY));
        assertFalse(instance.isSuitable(HALF_CAPACITY));
        assertTrue(instance.isSuitable(QUARTER_OF_CAPACITY));
    }    
}