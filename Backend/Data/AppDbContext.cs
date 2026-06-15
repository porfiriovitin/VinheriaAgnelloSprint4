using Microsoft.EntityFrameworkCore;
using VinheriaAgnelloCRUD.Data.Models;

namespace VinheriaAgnelloCRUD.Data;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<Wine> Wines => Set<Wine>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Wine>(entity =>
        {
            entity.ToTable("wines");

            entity.HasKey(w => w.Id);

            entity.Property(w => w.Name)
                .IsRequired()
                .HasMaxLength(120);

            entity.Property(w => w.Winery)
                .IsRequired()
                .HasMaxLength(80);

            entity.Property(w => w.Type)
                .IsRequired()
                .HasMaxLength(60);

            entity.Property(w => w.Country)
                .HasMaxLength(80);

            entity.Property(w => w.Quantity)
                .IsRequired();

            entity.Property(w => w.Price)
                .HasColumnType("decimal(10,2)")
                .IsRequired();

            entity.Property(w => w.CreatedAt)
                .IsRequired();

            entity.HasData(
                new Wine
                {
                    Id = Guid.Parse("79b4e22b-97c8-4e2a-9a26-c8d2a6e87211"),
                    Name = "Agnello Reserva Tinto",
                    Winery = "Vinheria Agnello",
                    Type = "Tinto",
                    Country = "Brasil",
                    Year = 2020,
                    Quantity = 24,
                    Price = 89.90m,
                    CreatedAt = new DateTime(2026, 6, 15, 0, 0, 0, DateTimeKind.Utc)
                },
                new Wine
                {
                    Id = Guid.Parse("e79d36a7-ea01-48a0-9c6d-a216d343b9ef"),
                    Name = "Agnello Branco Seco",
                    Winery = "Vinheria Agnello",
                    Type = "Branco",
                    Country = "Chile",
                    Year = 2022,
                    Quantity = 18,
                    Price = 74.50m,
                    CreatedAt = new DateTime(2026, 6, 15, 0, 0, 0, DateTimeKind.Utc)
                },
                new Wine
                {
                    Id = Guid.Parse("a1817271-51c8-4f62-8446-c41da16a9f0a"),
                    Name = "Agnello Rose",
                    Winery = "Vinheria Agnello",
                    Type = "Rose",
                    Country = "Argentina",
                    Year = 2021,
                    Quantity = 12,
                    Price = 69.90m,
                    CreatedAt = new DateTime(2026, 6, 15, 0, 0, 0, DateTimeKind.Utc)
                });
        });
    }

}
