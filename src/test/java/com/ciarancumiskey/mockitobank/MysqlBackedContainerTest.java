package com.ciarancumiskey.mockitobank;

import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.utils.TestConstants;
import com.ciarancumiskey.mockitobank.utils.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.stream.Stream;

import static com.ciarancumiskey.mockitobank.utils.TestConstants.MOCKITO_BANK_LATEST;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@Testcontainers
public class MysqlBackedContainerTest {

    @Container
    @ServiceConnection // connects the container to the app
    private static final MySQLContainer<?> mySQLContainer = TestUtils.getMySqlEmulatorContainer();

//fixme    @Container
//    private static final GenericContainer<?> mockitoBankContainer = new GenericContainer<>(MOCKITO_BANK_LATEST)
//            .withExposedPorts(8080)
//            .dependsOn(mySQLContainer);

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void setup() throws SQLException {
        mySQLContainer.start();
    }

    @Test
    public void testContainerIsRunning(){
        assertTrue(mySQLContainer.isCreated());
        assertTrue(mySQLContainer.isRunning());
    }

    @ParameterizedTest
    @MethodSource("createUsersParameters")
    public void testCreatingUsers(final String sortCode, final String accountName, final String expectedAccountName,
                                  final String accountNumber, final String emailAddress,
                                  final String expectedEmailAddress, final String expectedIbanCode) {
        final AccountCreationRequest acCreationReq = new AccountCreationRequest(sortCode, accountName, accountNumber,
                emailAddress);
        //fixme: uncomment this once the Docker image is created
//        String accountCreationResponseString = restTemplate
//                .postForObject("http://localhost:8080/accounts/register", acCreationReq, String.class);
//        assertNotNull(accountCreationResponseString);
    }

    @AfterAll
    public static void cleanup(){
        mySQLContainer.stop();
    }

    private static Stream<Arguments> createUsersParameters() {
        return Stream.of(
            TestConstants.USER_1_ARGS,
            TestConstants.USER_2_ARGS,
            TestConstants.USER_3_ARGS,
            TestConstants.USER_4_ARGS
        );
    }
}
