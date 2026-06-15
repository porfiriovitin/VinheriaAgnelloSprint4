using System.ComponentModel.DataAnnotations;

namespace VinheriaAgnelloCRUD.Data.Models;

public class Wine
{
    public Guid Id { get; set; } = Guid.NewGuid();

    [Required]
    [MaxLength(120)]
    public string Name { get; set; } = string.Empty;

    [Required]
    [MaxLength(80)]
    public string Winery { get; set; } = string.Empty;

    [Required]
    [MaxLength(60)]
    public string Type { get; set; } = string.Empty;

    [MaxLength(80)]
    public string? Country { get; set; }

    public int? Year { get; set; }

    [Range(0, int.MaxValue)]
    public int Quantity { get; set; }

    [Range(0, double.MaxValue)]
    public decimal Price { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
