import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/toko";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Scanner scanner = new Scanner(System.in)) {
            System.out.println(" Koneksi ke database berhasil!");
            System.out.println("\n--- Masukkan Data Barang Baru ---");
            System.out.print("Kode Barang (misal: B001): ");
            String kode = scanner.nextLine();
            System.out.print("Nama Barang: ");
            String nama = scanner.nextLine();
            System.out.print("Harga Satuan: ");
            int harga = scanner.nextInt();
            System.out.print("Jumlah Stok: ");
            int stok = scanner.nextInt();

            String sqlInsert = "{CALL insert_barang(?, ?, ?, ?)}";
            try (CallableStatement cstmt = conn.prepareCall(sqlInsert)) {
                cstmt.setString(1, kode);
                cstmt.setString(2, nama);
                cstmt.setInt(3, harga);
                cstmt.setInt(4, stok);

                cstmt.executeUpdate();
                System.out.println("\n>> Sukses! Data barang berhasil dimasukkan.");
                System.out.println(">> Trigger telah otomatis mencatat log insert.");
            }
            System.out.println("\n--- Menampilkan Semua Data Barang (dari VIEW) ---");
            String sqlSelect = "SELECT kode, nama, harga, stok, total_nilai FROM view_barang_detail";
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlSelect)) {

                System.out.println("------------------------------------------------------------------");
                System.out.printf("%-10s | %-20s | %-10s | %-5s | %-15s\n", "KODE", "NAMA BARANG", "HARGA", "STOK", "TOTAL NILAI");
                System.out.println("------------------------------------------------------------------");

                while (rs.next()) {
                    String viewKode = rs.getString("kode");
                    String viewNama = rs.getString("nama");
                    int viewHarga = rs.getInt("harga");
                    int viewStok = rs.getInt("stok");
                    long viewTotalNilai = rs.getLong("total_nilai");

                    System.out.printf("%-10s | %-20s | %-10d | %-5d | %-15d\n",
                            viewKode, viewNama, viewHarga, viewStok, viewTotalNilai);
                }
                System.out.println("------------------------------------------------------------------");
            }

        } catch (SQLException e) {
            System.err.println("\n TERJADI ERROR!");
            System.err.println("Pesan Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}