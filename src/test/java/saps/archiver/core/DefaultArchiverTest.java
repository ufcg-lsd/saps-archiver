package saps.archiver.core;

import static org.junit.Assert.*;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import saps.archiver.interfaces.PermanentStorage;
import saps.archiver.interfaces.Archiver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CatalogUtils.class})
public class DefaultArchiverTest {
	
	Archiver DefaultArchiver;
	Properties properties;	
	
	@Mock
	PermanentStorage permanentStorage = mock(PermanentStorage.class);
	
	@Mock		
	Catalog catalog = mock(Catalog.class);
	
	@Mock
	SapsImage sapsImage1 = mock(SapsImage.class);
	
	@Mock
	SapsImage sapsImage2 = mock(SapsImage.class);
	
	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);
		properties = new Properties();
	}
	
	@Test
	public void archive() throws IOException {
		
		PowerMockito.mockStatic(CatalogUtils.class);
		DefaultArchiver = new DefaultArchiver(properties, catalog, permanentStorage);

		List<SapsImage> sapsImages = new ArrayList<SapsImage>();
		sapsImages.add(sapsImage1);
		sapsImages.add(sapsImage2);
		
		when(permanentStorage.archive(sapsImage1)).thenReturn(true);
		
		List<SapsImage> expectedList = new ArrayList<SapsImage>();
		expectedList.add(sapsImage1);
		
		when(CatalogUtils.getTasks(catalog, ImageTaskState.FINISHED)).thenReturn(expectedList);
		
		assertTrue(DefaultArchiver.archive().contains(sapsImage1));
		assertEquals(DefaultArchiver.archive(), expectedList);
		assertFalse(DefaultArchiver.archive().contains(sapsImage2));
	
	}
	
	@Test
	public void gc() {
		
		PowerMockito.mockStatic(CatalogUtils.class);
		DefaultArchiver = new DefaultArchiver(properties, catalog, permanentStorage);
		
		List<SapsImage> failedTasks = new ArrayList<>();
		failedTasks.add(sapsImage1);
		
		when(CatalogUtils.getTasks(catalog, ImageTaskState.FAILED)).thenReturn(failedTasks);
		
		assertTrue(DefaultArchiver.gc().contains(sapsImage1));
		assertEquals(DefaultArchiver.gc(), failedTasks);
	}


}
