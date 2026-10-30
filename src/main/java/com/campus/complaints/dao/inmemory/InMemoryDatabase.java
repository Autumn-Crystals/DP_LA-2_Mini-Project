package com.campus.complaints.dao.inmemory;

import com.campus.complaints.dao.*;
import com.campus.complaints.designpatterns.memento.ComplaintMemento;
import com.campus.complaints.model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation that provides seamless fallback
 * when MySQL is offline. Allows full demonstration of all 6 GoF design patterns
 * and MVC architecture without external setup dependencies.
 */
public class InMemoryDatabase {

    public static class InMemoryUserDAO implements UserDAO {
        private final Map<Integer, User> usersById = new HashMap<>();
        private final Map<String, User> usersByEmail = new HashMap<>();
        private final AtomicInteger idGen = new AtomicInteger(10);

        @Override
        public Optional<User> findByEmail(String email) {
            if (email == null) return Optional.empty();
            return Optional.ofNullable(usersByEmail.get(email.trim().toLowerCase()));
        }

        @Override
        public Optional<User> findById(int id) {
            return Optional.ofNullable(usersById.get(id));
        }

        @Override
        public User save(User user) {
            if (user.getId() <= 0) {
                user.setId(idGen.getAndIncrement());
            }
            usersById.put(user.getId(), user);
            usersByEmail.put(user.getEmail().trim().toLowerCase(), user);
            return user;
        }

        @Override
        public boolean emailExists(String email) {
            return usersByEmail.containsKey(email.trim().toLowerCase());
        }
    }

    public static class InMemoryComplaintDAO implements ComplaintDAO {
        private final Map<Integer, Complaint> storage = new LinkedHashMap<>();
        private final AtomicInteger idGen = new AtomicInteger(100);

        @Override
        public Complaint save(Complaint complaint) {
            if (complaint.getId() <= 0) {
                complaint.setId(idGen.getAndIncrement());
            }
            storage.put(complaint.getId(), complaint);
            return complaint;
        }

        @Override
        public Optional<Complaint> findById(int id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Complaint> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public List<Complaint> findByReporterId(int reporterId) {
            return storage.values().stream()
                    .filter(c -> c.getReporterId() == reporterId)
                    .collect(Collectors.toList());
        }

        @Override
        public List<Complaint> findByStatus(ComplaintStatus status) {
            return storage.values().stream()
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        }

        @Override
        public List<Complaint> findByCategory(ComplaintCategory category) {
            return storage.values().stream()
                    .filter(c -> c.getCategory() == category)
                    .collect(Collectors.toList());
        }

        @Override
        public List<Complaint> findByAssignedStaffId(int staffId) {
            return storage.values().stream()
                    .filter(c -> c.getAssignedStaffId() != null && c.getAssignedStaffId() == staffId)
                    .collect(Collectors.toList());
        }

        @Override
        public int countActiveByStaffId(int staffId) {
            return (int) storage.values().stream()
                    .filter(c -> c.getAssignedStaffId() != null && c.getAssignedStaffId() == staffId)
                    .filter(c -> c.getStatus() != ComplaintStatus.RESOLVED && c.getStatus() != ComplaintStatus.CANCELLED)
                    .count();
        }

        @Override
        public boolean update(Complaint complaint) {
            if (!storage.containsKey(complaint.getId())) return false;
            storage.put(complaint.getId(), complaint);
            return true;
        }

        @Override
        public Map<ComplaintStatus, Integer> countByStatusForReporter(int reporterId) {
            Map<ComplaintStatus, Integer> counts = new EnumMap<>(ComplaintStatus.class);
            for (ComplaintStatus s : ComplaintStatus.values()) counts.put(s, 0);
            storage.values().stream()
                    .filter(c -> c.getReporterId() == reporterId)
                    .forEach(c -> counts.put(c.getStatus(), counts.get(c.getStatus()) + 1));
            return counts;
        }

        @Override
        public Map<ComplaintStatus, Integer> countByStatus() {
            Map<ComplaintStatus, Integer> counts = new EnumMap<>(ComplaintStatus.class);
            for (ComplaintStatus s : ComplaintStatus.values()) counts.put(s, 0);
            storage.values().forEach(c -> counts.put(c.getStatus(), counts.get(c.getStatus()) + 1));
            return counts;
        }

        @Override
        public List<Complaint> search(String query) {
            String q = query.toLowerCase();
            return storage.values().stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(q) || String.valueOf(c.getId()).contains(q))
                    .collect(Collectors.toList());
        }
    }

    public static class InMemoryStaffDAO implements StaffDAO {
        private final Map<Integer, MaintenanceStaff> staffMap = new LinkedHashMap<>();

        @Override
        public Optional<MaintenanceStaff> findById(int id) {
            return Optional.ofNullable(staffMap.get(id));
        }

        @Override
        public List<MaintenanceStaff> findAll() {
            return new ArrayList<>(staffMap.values());
        }

        @Override
        public List<MaintenanceStaff> findAllWithLoadAndSkills() {
            return new ArrayList<>(staffMap.values());
        }

        @Override
        public MaintenanceStaff save(MaintenanceStaff staff) {
            staffMap.put(staff.getId(), staff);
            return staff;
        }

        @Override
        public boolean update(MaintenanceStaff staff) {
            staffMap.put(staff.getId(), staff);
            return true;
        }

        @Override
        public void updateSkills(int staffId, List<ComplaintCategory> skills) {
            MaintenanceStaff staff = staffMap.get(staffId);
            if (staff != null) {
                staff.setSkills(new ArrayList<>(skills));
            }
        }
    }

    public static class InMemoryComplaintHistoryDAO extends ComplaintHistoryDAO {
        private final Map<Integer, List<ComplaintMemento>> history = new HashMap<>();

        @Override
        public void saveHistory(ComplaintMemento memento, String previousStateJson, String newStateJson) {
            history.computeIfAbsent(memento.getComplaintId(), k -> new ArrayList<>()).add(0, memento);
        }

        @Override
        public List<ComplaintMemento> loadHistory(int complaintId, int limit) {
            List<ComplaintMemento> list = history.getOrDefault(complaintId, Collections.emptyList());
            if (list.size() <= limit) return new ArrayList<>(list);
            return new ArrayList<>(list.subList(0, limit));
        }
    }

    /**
     * Seeds in-memory DAOs with rich realistic campus data.
     */
    public static void seed(InMemoryUserDAO userDAO, InMemoryComplaintDAO complaintDAO, InMemoryStaffDAO staffDAO) {
        String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt(10));
        String studentHash = BCrypt.hashpw("student123", BCrypt.gensalt(10));

        // Users
        User admin = new User(1, "Campus Administrator", "admin@campus.edu", adminHash, UserRole.ADMIN, LocalDateTime.now());
        User student1 = new User(2, "Student 1", "student1@campus.edu", studentHash, UserRole.STUDENT, LocalDateTime.now());
        User student2 = new User(3, "Student 2", "student2@campus.edu", studentHash, UserRole.STUDENT, LocalDateTime.now());
        User alice = new User(4, "Alice Johnson", "alice@student.edu", studentHash, UserRole.STUDENT, LocalDateTime.now());

        userDAO.save(admin);
        userDAO.save(student1);
        userDAO.save(student2);
        userDAO.save(alice);

        // Maintenance Staff
        MaintenanceStaff ravi = new MaintenanceStaff(1, 6, "Ravi Kumar", "ravi@staff.edu", "9000000001", MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
        ravi.addSkill(ComplaintCategory.ELECTRICAL);
        ravi.addSkill(ComplaintCategory.GENERAL);
        ravi.setActiveComplaintCount(1);

        MaintenanceStaff priya = new MaintenanceStaff(2, 7, "Priya Singh", "priya@staff.edu", "9000000002", MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
        priya.addSkill(ComplaintCategory.NETWORK);
        priya.addSkill(ComplaintCategory.IT_SUPPORT);
        priya.setActiveComplaintCount(1);

        MaintenanceStaff johnC = new MaintenanceStaff(3, 8, "John Carter", "john@staff.edu", "9000000003", MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
        johnC.addSkill(ComplaintCategory.PLUMBING);
        johnC.addSkill(ComplaintCategory.CLEANING);
        johnC.setActiveComplaintCount(2);

        MaintenanceStaff meena = new MaintenanceStaff(4, 9, "Meena Nair", "meena@staff.edu", "9000000004", MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
        meena.addSkill(ComplaintCategory.EQUIPMENT);
        meena.addSkill(ComplaintCategory.ELECTRICAL);
        meena.setActiveComplaintCount(0);

        staffDAO.save(ravi);
        staffDAO.save(priya);
        staffDAO.save(johnC);
        staffDAO.save(meena);

        // Complaints for Student 1 (reporterId = 2) -> 2 complaints
        complaintDAO.save(new Complaint.Builder()
                .id(1)
                .title("Wi-Fi Not Working in Computer Lab 2")
                .description("Students cannot connect to campus Wi-Fi in Lab 2. Access point offline.")
                .category(ComplaintCategory.NETWORK)
                .location("Computer Lab 2, 2nd Floor")
                .priority(Priority.HIGH)
                .status(ComplaintStatus.PENDING)
                .reporterId(2)
                .build());

        complaintDAO.save(new Complaint.Builder()
                .id(2)
                .title("Water Leakage in Boys Washroom B")
                .description("Continuous water leakage from pipe near sink on Ground Floor.")
                .category(ComplaintCategory.PLUMBING)
                .location("Washroom B, Ground Floor")
                .priority(Priority.HIGH)
                .status(ComplaintStatus.IN_PROGRESS)
                .assignedStaffId(3)
                .reporterId(2)
                .build());

        // Complaints for Student 2 (reporterId = 3) -> 3 complaints
        complaintDAO.save(new Complaint.Builder()
                .id(3)
                .title("Broken Ceiling Fan in Room 301")
                .description("The ceiling fan stopped working. Affecting lectures in afternoon heat.")
                .category(ComplaintCategory.ELECTRICAL)
                .location("Classroom 301, Block A")
                .priority(Priority.MEDIUM)
                .status(ComplaintStatus.ASSIGNED)
                .assignedStaffId(1)
                .reporterId(3)
                .build());

        complaintDAO.save(new Complaint.Builder()
                .id(4)
                .title("Projector Lamp Blown in Seminar Hall")
                .description("Projector lamp is blown, cannot conduct scheduled guest seminar.")
                .category(ComplaintCategory.EQUIPMENT)
                .location("Seminar Hall, Admin Block")
                .priority(Priority.CRITICAL)
                .status(ComplaintStatus.RESOLVED)
                .assignedStaffId(4)
                .reporterId(3)
                .resolvedAt(LocalDateTime.now().minusDays(1))
                .resolutionNotes("Replaced lamp module and tested HDMI connection.")
                .build());

        complaintDAO.save(new Complaint.Builder()
                .id(5)
                .title("Flickering Lights in Library Reading Hall")
                .description("Multiple fluorescent lights flickering continuously, causing eye strain.")
                .category(ComplaintCategory.ELECTRICAL)
                .location("Central Library, Reading Area")
                .priority(Priority.MEDIUM)
                .status(ComplaintStatus.PENDING)
                .reporterId(3)
                .build());

        // Additional campus complaint
        complaintDAO.save(new Complaint.Builder()
                .id(6)
                .title("Overflowing Bins Near Canteen")
                .description("Sanitation bins are overflowing causing hygiene problems.")
                .category(ComplaintCategory.CLEANING)
                .location("Canteen Courtyard")
                .priority(Priority.MEDIUM)
                .status(ComplaintStatus.ASSIGNED)
                .assignedStaffId(3)
                .reporterId(4)
                .build());
    }
}
