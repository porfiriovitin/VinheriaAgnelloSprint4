namespace VinheriaAgnelloCRUD.DTOs.Wines;

/// <summary>
/// Representa um vinho retornado pela API.
/// </summary>
public class WineResponse
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Winery { get; set; } = string.Empty;
    public string Type { get; set; } = string.Empty;
    public string? Country { get; set; }
    public int? Year { get; set; }
    public int Quantity { get; set; }
    public decimal Price { get; set; }
    public DateTime CreatedAt { get; set; }
}
