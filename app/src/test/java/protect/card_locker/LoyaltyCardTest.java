package protect.card_locker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;
import java.util.Currency;

@RunWith(RobolectricTestRunner.class)
public class LoyaltyCardTest {

    private Context mockContext;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
    }

    /**
     * Test 1: Test default constructor creates card with correct default values
     * Important for ensuring duplicated cards start with proper defaults
     */
    @Test
    public void testDefaultConstructorInitialization() {
        LoyaltyCard card = new LoyaltyCard();

        assertEquals("Default ID should be -1", -1, card.id);
        assertEquals("Default store should be empty", "", card.store);
        assertEquals("Default note should be empty", "", card.note);
        assertNull("Default validFrom should be null", card.validFrom);
        assertNull("Default expiry should be null", card.expiry);
        assertEquals("Default balance should be 0", new BigDecimal("0"), card.balance);
        assertNull("Default balanceType should be null", card.balanceType);
        assertEquals("Default cardId should be empty", "", card.cardId);
        assertNull("Default barcodeId should be null", card.barcodeId);
        assertNull("Default barcodeType should be null", card.barcodeType);
        assertNull("Default headerColor should be null", card.headerColor);
        assertEquals("Default starStatus should be 0", 0, card.starStatus);
        assertEquals("Default archiveStatus should be 0", 0, card.archiveStatus);
        assertEquals("Default zoomLevel should be 100", 100, card.zoomLevel);
        assertEquals("Default zoomLevelWidth should be 100", 100, card.zoomLevelWidth);
    }

    /**
     * Test 2: Test isDuplicate method with identical cards (fake data)
     * Critical for card duplication detection
     */
    @Test
    public void testIsDuplicateWithIdenticalCards() {
        // Create two identical fake cards
        LoyaltyCard card1 = new LoyaltyCard();
        card1.id = 1;
        card1.store = "Test Store";
        card1.note = "Test Note";
        card1.cardId = "123456";
        card1.balance = new BigDecimal("100");
        card1.starStatus = 1;
        card1.archiveStatus = 0;
        card1.headerColor = Color.BLUE;

        LoyaltyCard card2 = new LoyaltyCard();
        card2.id = 1;
        card2.store = "Test Store";
        card2.note = "Test Note";
        card2.cardId = "123456";
        card2.balance = new BigDecimal("100");
        card2.starStatus = 1;
        card2.archiveStatus = 0;
        card2.headerColor = Color.BLUE;

        assertTrue("Identical cards should be detected as duplicates",
                LoyaltyCard.isDuplicate(mockContext, card1, card2));
    }

    /**
     * Test 3: Test isDuplicate method with different cards (fake data)
     * Ensures non-duplicates are correctly identified
     */
    @Test
    public void testIsDuplicateWithDifferentCards() {
        LoyaltyCard card1 = new LoyaltyCard();
        card1.id = 1;
        card1.store = "Store A";
        card1.cardId = "111111";
        card1.balance = new BigDecimal("50");

        LoyaltyCard card2 = new LoyaltyCard();
        card2.id = 1;
        card2.store = "Store B";
        card2.cardId = "222222";
        card2.balance = new BigDecimal("75");

        assertFalse("Different cards should not be detected as duplicates",
                LoyaltyCard.isDuplicate(mockContext, card1, card2));
    }

    /**
     * Test 4: Test setStarStatus validation
     * Important for duplication to preserve star status correctly
     */
    @Test
    public void testSetStarStatusValidation() {
        LoyaltyCard card = new LoyaltyCard();

        // Valid values
        card.setStarStatus(0);
        assertEquals("Star status should be 0", 0, card.starStatus);

        card.setStarStatus(1);
        assertEquals("Star status should be 1", 1, card.starStatus);

        // Invalid value should throw exception
        try {
            card.setStarStatus(2);
            fail("Should throw IllegalArgumentException for invalid starStatus");
        } catch (IllegalArgumentException e) {
            assertEquals("starStatus must be 0 or 1", e.getMessage());
        }

        try {
            card.setStarStatus(-1);
            fail("Should throw IllegalArgumentException for negative starStatus");
        } catch (IllegalArgumentException e) {
            assertEquals("starStatus must be 0 or 1", e.getMessage());
        }
    }

    /**
     * Test 5: Test setArchiveStatus validation
     * Critical for card duplication to handle archived cards correctly
     */
    @Test
    public void testSetArchiveStatusValidation() {
        LoyaltyCard card = new LoyaltyCard();

        // Valid values
        card.setArchiveStatus(0);
        assertEquals("Archive status should be 0", 0, card.archiveStatus);

        card.setArchiveStatus(1);
        assertEquals("Archive status should be 1", 1, card.archiveStatus);

        // Invalid value should throw exception
        try {
            card.setArchiveStatus(5);
            fail("Should throw IllegalArgumentException for invalid archiveStatus");
        } catch (IllegalArgumentException e) {
            assertEquals("archiveStatus must be 0 or 1", e.getMessage());
        }
    }

    /**
     * Test 6: Test setZoomLevel validation
     * Ensures zoom settings are preserved during duplication
     */
    @Test
    public void testSetZoomLevelValidation() {
        LoyaltyCard card = new LoyaltyCard();

        // Valid values
        card.setZoomLevel(0);
        assertEquals("Zoom level should be 0", 0, card.zoomLevel);

        card.setZoomLevel(50);
        assertEquals("Zoom level should be 50", 50, card.zoomLevel);

        card.setZoomLevel(100);
        assertEquals("Zoom level should be 100", 100, card.zoomLevel);

        // Invalid values
        try {
            card.setZoomLevel(-1);
            fail("Should throw IllegalArgumentException for negative zoom");
        } catch (IllegalArgumentException e) {
            assertEquals("zoomLevel must be in range 0-100", e.getMessage());
        }

        try {
            card.setZoomLevel(101);
            fail("Should throw IllegalArgumentException for zoom > 100");
        } catch (IllegalArgumentException e) {
            assertEquals("zoomLevel must be in range 0-100", e.getMessage());
        }
    }

    /**
     * Test 7: Test nullableBitmapsEqual with various bitmap combinations
     * Critical for image comparison during card duplication
     */
    @Test
    public void testNullableBitmapsEqual() {
        // Both null
        assertTrue("Two null bitmaps should be equal",
                LoyaltyCard.nullableBitmapsEqual(null, null));

        // Mock bitmaps for testing
        Bitmap mockBitmap1 = mock(Bitmap.class);
        Bitmap mockBitmap2 = mock(Bitmap.class);

        // One null, one not null
        assertFalse("Null and non-null bitmap should not be equal",
                LoyaltyCard.nullableBitmapsEqual(null, mockBitmap1));
        assertFalse("Non-null and null bitmap should not be equal",
                LoyaltyCard.nullableBitmapsEqual(mockBitmap1, null));

        // Both not null - same
        when(mockBitmap1.sameAs(mockBitmap2)).thenReturn(true);
        assertTrue("Same bitmaps should be equal",
                LoyaltyCard.nullableBitmapsEqual(mockBitmap1, mockBitmap2));

        // Both not null - different
        when(mockBitmap1.sameAs(mockBitmap2)).thenReturn(false);
        assertFalse("Different bitmaps should not be equal",
                LoyaltyCard.nullableBitmapsEqual(mockBitmap1, mockBitmap2));
    }

    /**
     * Test 8: Test updateFromBundle for card duplication workflow
     * Uses fake Bundle data to simulate card data transfer during duplication
     */
    @Test
    public void testUpdateFromBundleForDuplication() {
        LoyaltyCard card = new LoyaltyCard();

        // Create fake bundle with card data
        Bundle bundle = new Bundle();
        bundle.putInt(LoyaltyCard.BUNDLE_LOYALTY_CARD_ID, 10);
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_STORE, "Duplicated Store");
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_NOTE, "Duplicated Note");
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_BALANCE, "200.50");
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_BALANCE_TYPE, "USD");
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_CARD_ID, "DUP123456");
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_BARCODE_ID, "DUPBAR123");
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_BARCODE_TYPE, "QR_CODE");
        bundle.putInt(LoyaltyCard.BUNDLE_LOYALTY_CARD_HEADER_COLOR, Color.RED);
        bundle.putInt(LoyaltyCard.BUNDLE_LOYALTY_CARD_STAR_STATUS, 1);
        bundle.putLong(LoyaltyCard.BUNDLE_LOYALTY_CARD_LAST_USED, 1234567890L);
        bundle.putInt(LoyaltyCard.BUNDLE_LOYALTY_CARD_ZOOM_LEVEL, 75);
        bundle.putInt(LoyaltyCard.BUNDLE_LOYALTY_CARD_ZOOM_LEVEL_WIDTH, 80);
        bundle.putInt(LoyaltyCard.BUNDLE_LOYALTY_CARD_ARCHIVE_STATUS, 0);
        bundle.putLong(LoyaltyCard.BUNDLE_LOYALTY_CARD_VALID_FROM, -1);
        bundle.putLong(LoyaltyCard.BUNDLE_LOYALTY_CARD_EXPIRY, -1);
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_IMAGE_THUMBNAIL, null);
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_IMAGE_FRONT, null);
        bundle.putString(LoyaltyCard.BUNDLE_LOYALTY_CARD_IMAGE_BACK, null);

        // Update card from bundle
        card.updateFromBundle(bundle, true);

        // Verify all fields were updated correctly
        assertEquals("ID should be updated", 10, card.id);
        assertEquals("Store should be updated", "Duplicated Store", card.store);
        assertEquals("Note should be updated", "Duplicated Note", card.note);
        assertEquals("Balance should be updated", new BigDecimal("200.50"), card.balance);
        assertEquals("Balance type should be USD", Currency.getInstance("USD"), card.balanceType);
        assertEquals("Card ID should be updated", "DUP123456", card.cardId);
        assertEquals("Barcode ID should be updated", "DUPBAR123", card.barcodeId);
        assertNotNull("Barcode type should not be null", card.barcodeType);
        assertEquals("Header color should be red", Integer.valueOf(Color.RED), card.headerColor);
        assertEquals("Star status should be 1", 1, card.starStatus);
        assertEquals("Last used should be updated", 1234567890L, card.lastUsed);
        assertEquals("Zoom level should be 75", 75, card.zoomLevel);
        assertEquals("Zoom level width should be 80", 80, card.zoomLevelWidth);
        assertEquals("Archive status should be 0", 0, card.archiveStatus);
    }
}