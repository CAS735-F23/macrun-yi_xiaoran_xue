package com.course.project.challengecenter.finder;

import com.course.project.challengecenter.business.BadgeRegistry;
import com.course.project.challengecenter.business.entity.Badges;
import com.course.project.challengecenter.port.BadgeFinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TestBadgeFinder {
    @Mock
    private BadgeFinder badgeFinder;
    private LocalDateTime dateTime;

    @Before
    public void init() {
        dateTime = LocalDateTime.now();

        Badges badge = new Badges((long) 1, (long) 2, "Advanced Badge", dateTime);

        List<Badges> badges = new ArrayList<Badges>();

        badges.add(badge);

        Mockito.when(badgeFinder.findByUserId((long) 2)).
                thenReturn(badges);

        Mockito.when(badgeFinder.findAll()).
                thenReturn(badges);
    }

    @Test
    public void testFindAllBadges_returnsCorrectListOfBadges() {
        List<Badges> badges = badgeFinder.findAll();

        assertEquals(badges.size(), 1);
        assertEquals(badges.get(0).getName(), "Advanced Badge");
    }

    @Test
    public void testFindBadgesByUser_returnsCorrectListOfBadges() {
        List<Badges> badges = badgeFinder.findByUserId((long) 2);

        assertEquals(badges.size(), 1);
        assertEquals(badges.get(0).getName(), "Advanced Badge");
    }

    @TestConfiguration
    static class TestBadgeFinderContextConfiguration {

        @Bean
        public BadgeFinder badgeRegistry() {
            return new BadgeRegistry();
        }

    }


}
