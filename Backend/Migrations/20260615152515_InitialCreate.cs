using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace VinheriaAgnelloCRUD.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "wines",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    Name = table.Column<string>(type: "TEXT", maxLength: 120, nullable: false),
                    Winery = table.Column<string>(type: "TEXT", maxLength: 80, nullable: false),
                    Type = table.Column<string>(type: "TEXT", maxLength: 60, nullable: false),
                    Country = table.Column<string>(type: "TEXT", maxLength: 80, nullable: true),
                    Year = table.Column<int>(type: "INTEGER", nullable: true),
                    Quantity = table.Column<int>(type: "INTEGER", nullable: false),
                    Price = table.Column<decimal>(type: "decimal(10,2)", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "TEXT", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_wines", x => x.Id);
                });

            migrationBuilder.InsertData(
                table: "wines",
                columns: new[] { "Id", "Country", "CreatedAt", "Name", "Price", "Quantity", "Type", "Winery", "Year" },
                values: new object[,]
                {
                    { new Guid("79b4e22b-97c8-4e2a-9a26-c8d2a6e87211"), "Brasil", new DateTime(2026, 6, 15, 0, 0, 0, 0, DateTimeKind.Utc), "Agnello Reserva Tinto", 89.90m, 24, "Tinto", "Vinheria Agnello", 2020 },
                    { new Guid("a1817271-51c8-4f62-8446-c41da16a9f0a"), "Argentina", new DateTime(2026, 6, 15, 0, 0, 0, 0, DateTimeKind.Utc), "Agnello Rose", 69.90m, 12, "Rose", "Vinheria Agnello", 2021 },
                    { new Guid("e79d36a7-ea01-48a0-9c6d-a216d343b9ef"), "Chile", new DateTime(2026, 6, 15, 0, 0, 0, 0, DateTimeKind.Utc), "Agnello Branco Seco", 74.50m, 18, "Branco", "Vinheria Agnello", 2022 }
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "wines");
        }
    }
}
